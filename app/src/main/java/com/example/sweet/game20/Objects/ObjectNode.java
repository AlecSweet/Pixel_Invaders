package com.example.sweet.game20.Objects;

/**
 * Created by Sweet on 8/3/2018.
 */

public class ObjectNode
{
    public Object object;
    public ObjectNode nextObject;
    
    public ObjectNode(Object o, ObjectNode nO)
    {
        object = o;
        nextObject = nO;
    }
    
    public ObjectNode()
    {
        
    }
}
