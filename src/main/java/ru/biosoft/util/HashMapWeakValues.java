package ru.biosoft.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO Doc
 */
public class HashMapWeakValues extends ConcurrentHashMap
{
    final static int checkInterval = 512;

    int checkCounter = 0;
    ReferenceQueue queue = new ReferenceQueue();

    public HashMapWeakValues()
    {}

    @Override
    public Object get( Object key )
    {
        Reference ref = (Reference)super.get(key);
        return ref==null?null:ref.get();
    }

    @Override
    public Object put( Object key, Object value )
    {
        if( ++checkCounter >= checkInterval )
        {
            checkCounter = 0;
            WeakValueRef r;
            while( (r=(WeakValueRef)queue.poll())!=null )
            {
                if( get(r.key)==null )
                    remove(r.key);
            }
        }

        Reference ref = (Reference)super.put(key, new WeakValueRef(key,value,queue));
        return ref==null?null:ref.get();
    }

    @Override
    public Object remove(Object key)
    {
        Reference ref = (Reference)super.remove( key );
        return ref==null?null:ref.get();
    }

    /**
     * TODO oprimize???
     */
    @Override
    public Collection values()
    {
        ArrayList<Object> list = new ArrayList<>();
        Iterator<Reference<?>> i = super.values().iterator();
        while( i.hasNext() )
        {
            Reference<?> ref = i.next();
            Object obj = ref == null ? null : ref.get();
            if( obj != null )
                list.add(obj);
        }

        return list;
    }

    ////////////////////////////////////////////////////////////////////////////
    //  Inner classes
    //
    
    protected static class WeakValueRef extends WeakReference
    {
        Object key;

        public WeakValueRef(Object key, Object value, ReferenceQueue queue)
        {
            super( value,queue );
            this.key = key;

        }
    }

}