package ru.biosoft.util;

import java.util.concurrent.Callable;

/**
 * Utility class for lazy values.
 */
public class LazyValue<T>
{
    private volatile boolean initialized;
    private boolean initializing;
    private T value = null;
    private final String title;
    private final Callable<T> supplier;
    
    public LazyValue(String title, Callable<T> supplier)
    {
        this.title = title;
        this.supplier = supplier;
    }

    public LazyValue(String title)
    {
        this(title, null);
    }
    
    public LazyValue(Callable<T> supplier)
    {
        this(new Exception().getStackTrace()[1].getClassName(), supplier);
    }
    
    public LazyValue()
    {
        this(null, null);
    }
    
    protected T doGet() throws Exception
    {
        if(supplier == null)
        {
            throw new RuntimeException("LazyValue.doGet must be redefined when supplier is not supplied, title=" + title);
        }
        return supplier.call();
    }
    
    public final T get()
    {
        if(!initialized)
        {
            synchronized(this)
            {
                if(!initialized)
                {
                    if(initializing)
                        throw new RuntimeException("LazyValue concurrent initialization, title=" + title);
                    try
                    {
                        initializing = true;
                        value = doGet();
                    }
                    catch(Throwable t)
                    {
                        throw new RuntimeException("LazyValue initialization exception, title=" + title, t);
                    }
                    finally
                    {
                        initializing = false;
                    }
                    initialized = true;
                }
            }
        }

        if(value == null)
        {
            throw new RuntimeException("LazyValue initialization exception - value is null, title=" + title);
        }

        return value;
    }
    
    public final boolean isInitialized()
    {
        return initialized;
    }
}
