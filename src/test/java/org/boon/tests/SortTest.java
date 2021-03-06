package org.boon.tests;

import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.core.reflection.BeanUtils;
import org.boon.criteria.internal.Sort;
import org.boon.criteria.internal.SortType;
import org.boon.tests.model.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


import static org.junit.Assert.assertEquals;

public class SortTest {

    List<Employee> list;

    @Before
    public void setUp() throws Exception {
        list = Lists.list(
                Employee.employee( "zzz", "LastA", "120", "5.29.1970:00:00:01", 100 ),
                Employee.employee( "zaaa", "bbb", "124", "5.29.1960:00:00:00", 200 ),
                Employee.employee( "zaaa", "aaa", "123", "5.29.1970:00:00:01", 100 ),
                Employee.employee( "bababa", "LastB", "125", "5.29.1960:00:00:00", 200 ),
                Employee.employee( "BAbaba", "LastB", "126", "5.29.1960:00:00:00", 200 )

        );


    }

    @Test
    public void simpleSort() throws Exception {
        Sort sort = new Sort( "firstName", SortType.ASCENDING );
        sort.sort( list );
        List<String> firstNames = BeanUtils.idxList( Typ.string, list, "firstName" );
        assertEquals( "bababa", firstNames.get( 0 ) );
        assertEquals( "BAbaba", firstNames.get( 1 ) );
        assertEquals( "zaaa", firstNames.get( 2 ) );
        assertEquals( "zaaa", firstNames.get( 3 ) );
        assertEquals( "zzz", firstNames.get( 4 ) );

    }

    @Test
    public void compoundSort() throws Exception {
        Sort sort = new Sort( "firstName", SortType.ASCENDING );
        sort.then( "lastName" );
        sort.sort( list );
        List<String> firstNames = BeanUtils.idxList( Typ.string, list, "firstName" );
        List<String> lastNames = BeanUtils.idxList( Typ.string, list, "lastName" );

        assertEquals( "bababa", firstNames.get( 0 ) );
        assertEquals( "BAbaba", firstNames.get( 1 ) );
        assertEquals( "zaaa", firstNames.get( 2 ) );
        assertEquals( "zaaa", firstNames.get( 3 ) );
        assertEquals( "zzz", firstNames.get( 4 ) );

    }

}
