package org.boon.core.value;

import org.boon.core.Value;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValueList extends AbstractList<Object> implements List<Object> {

    List<Object> list = new ArrayList<>( 5 );

    private final boolean lazyChop;
    boolean converted = false;



    public ValueList( boolean lazyChop ) {
        this.lazyChop = lazyChop;
    }

    @Override
    public Object get( int index ) {

        Object obj = list.get( index );

        if ( obj instanceof Value ) {
            obj = convert( ( Value ) obj );
            list.set( index, obj );
        }

        chopIfNeeded( obj );
        return obj;

    }


    private Object convert( Value value ) {
        return value.toValue();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override

    public Iterator<Object> iterator() {
        convertAllIfNeeded();
        return list.iterator();
    }


    private void convertAllIfNeeded() {
        if ( !converted ) {
            converted = true;
            for ( int index = 0; index < list.size(); index++ ) {
                this.get( index );
            }
        }

    }


    @Override
    public void clear() {
        list.clear();
    }


    public boolean add( Object obj ) {
        return list.add( obj );
    }


    public void chopList() {

        for ( Object obj : list ) {
            if ( obj == null ) continue;

            if ( obj instanceof Value ) {
                Value value = ( Value ) obj;
                if ( value.isContainer() ) {
                    chopContainer( value );
                } else {
                    value.chop();
                }
            }
        }
    }

    private void chopIfNeeded( Object object ) {
        if ( lazyChop ) {
            if ( object instanceof LazyValueMap ) {
                LazyValueMap m = ( LazyValueMap ) object;
                m.chopMap();
            } else if ( object instanceof ValueList ) {
                ValueList list = ( ValueList ) object;
                list.chopList();
            }
        }

    }


    void chopContainer( Value value ) {
        Object obj = value.toValue();
        if ( obj instanceof LazyValueMap ) {
            LazyValueMap map = ( LazyValueMap ) obj;
            map.chopMap();
        } else if ( obj instanceof ValueList ) {
            ValueList list = ( ValueList ) obj;
            list.chopList();
        }
    }

    public List<Object> list () {
        return this.list;
    }
}
