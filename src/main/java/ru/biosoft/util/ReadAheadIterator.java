package ru.biosoft.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator implementation helper which reads one element ahead.
 */
public abstract class ReadAheadIterator<E> implements Iterator<E>
{
    private E cur;
    private boolean first = true;

    @Override
    public boolean hasNext()
    {
        if(first)
        {
            cur = advance();
            first = false;
        }
        return cur != null;
    }

    @Override
    public E next()
    {
        if(!hasNext())
            throw new NoSuchElementException();
        E result = cur;
        cur = advance();
        return result;
    }

    /**
     * @return object of type E for next object or null if iteration is finished
     * After this method returns null it will be never called anymore.
     * Must not throw any exception
     */
    protected abstract E advance();
}
