package org.boon.core.reflection.fields;


import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public interface FieldsAccessor {
      Map<String, FieldAccess> getFields ( Class<? extends Object> aClass );

}
