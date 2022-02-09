package ru.biosoft.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Map.Entry;

/**
 * Unmodifiable List of Strings which can load in memory via chunks.
 */
public abstract class ChunkedList<T extends Comparable<? super T>> extends AbstractList<T> implements RandomAccess
{
    protected final HashMap<Integer, Reference<T[]>> subLists = new HashMap<>();
    // lastChunk is stored via strong reference to increase cache hits
    private T[] lastChunk;
    protected int size;
    private boolean sorted;
    protected final int chunkSize;
    
    public ChunkedList(int size, int chunkSize, boolean sorted)
    {
        this.size = size;
        this.sorted = sorted;
        this.chunkSize = chunkSize;
    }
    
    public ChunkedList(int size)
    {
        this(size, 10000, false);
    }

    @Override
    public T get(int index)
    {
        if((size > -1 && index >= size) || index<0) throw new ArrayIndexOutOfBoundsException(index);
        int chunkNum = index/chunkSize;
        synchronized(subLists)
        {
            Reference<T[]> ref = subLists.get(chunkNum);
            lastChunk = ref == null ? null : ref.get();
            if(lastChunk == null)
            {
                lastChunk = getChunk(chunkNum*chunkSize, Math.min(chunkNum*chunkSize+chunkSize, size));
                if(lastChunk == null) return null;
                subLists.put(chunkNum, createChunkReference(lastChunk));
            }
            return lastChunk[index%chunkSize];
        }
    }

    protected SoftReference<T[]> createChunkReference(T[] chunk)
    {
        return new SoftReference<>( chunk );
    }

    /**
     * @param from  the chunk start position in this list
     * @param to    the chunk end position in this list
     * @return array of list elements from "from" to "to" (excluding)
     */
    abstract protected T[] getChunk(int from, int to);

    @Override
    public int size()
    {
        if(size == -1)
            get(0);
        return size;
    }

    @Override
    public String toString()
    {
        Iterator<T> i = iterator();
        if( !i.hasNext() )
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for( ;; )
        {
            String e = String.valueOf(i.next());
            sb.append(e);
            if( !i.hasNext() )
                return sb.append(']').toString();
            sb.append(", ");
            if(sb.length() > 10000)
            {
                sb.append("...");
                return sb.toString();
            }
        }
    }

    @Override
    public int indexOf(Object o)
    {
        // Use binary search if list is pre-sorted
        if(sorted)
        {
            int pos = Collections.binarySearch(this, (T)o);
            return pos >= 0 ? pos : -1; // to fulfill the contract of indexOf we must return -1, not any negative value
        }
        // Look first in the loaded chunks
        for(Entry<Integer, Reference<T[]>> entry: subLists.entrySet())
        {
            T[] chunk = entry.getValue().get();
            if(chunk == null) continue;
            for(int i=0; i<chunk.length; i++)
            {
                if(chunk[i].equals(o)) return i+entry.getKey()*chunkSize;
            }
        }
        return super.indexOf(o);
    }

    @Override
    public boolean contains(Object o)
    {
        return indexOf(o) >= 0;
    }
    
    public void invalidate()
    {
        synchronized(subLists)
        {
            subLists.clear();
        }
        lastChunk = null;
    }
}
