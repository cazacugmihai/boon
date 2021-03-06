package org.boon.core.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by rick on 12/15/13.
 * @author  Stéphane Landelle
 * J'ai écrit JSON parser du Boon. Sans Stéphane, l'analyseur n'existerait pas. Stéphane est la muse de Boon JSON, et mon entraîneur pour l'open source, github, et plus encore. Stéphane n'est pas le créateur directe, mais il est le maître architecte et je l'appelle mon ami.
 */
public class FastStringUtils {

    public static final Unsafe UNSAFE;
    public static final long STRING_VALUE_FIELD_OFFSET;
    public static final long STRING_OFFSET_FIELD_OFFSET;
    public static final long STRING_COUNT_FIELD_OFFSET;
    public static final boolean ENABLED;

    private static final boolean WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean(System.getProperty("org.boon.write.to.final.fields", "false"));
    private static final boolean DISABLE = Boolean.parseBoolean(System.getProperty("org.boon.faststringutils", "false"));

    private static Unsafe loadUnsafe() {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);

        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    static {
        UNSAFE = DISABLE ? null : loadUnsafe();
        ENABLED = UNSAFE != null;
    }

    private static long getFieldOffset(String fieldName) {
        if (ENABLED) {
            try {
                return UNSAFE.objectFieldOffset(String.class.getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                // field undefined
            }
        }
        return -1L;
    }

    static {
        STRING_VALUE_FIELD_OFFSET = getFieldOffset("value");
        STRING_OFFSET_FIELD_OFFSET = getFieldOffset("offset");
        STRING_COUNT_FIELD_OFFSET = getFieldOffset("count");
    }

    private enum StringImplementation {
        DIRECT_CHARS {
            @Override
            public char[] toCharArray(String string) {
                return (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);
            }
            
            @Override
            public String noCopyStringFromChars(char[] chars) {
                if (WRITE_TO_FINAL_FIELDS) {
                    String string = new String();
                    UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars);
                    return string;
                } else {
                    return new String(chars);
                }
            }
        },
        OFFSET {
            @Override
            public char[] toCharArray(String string) {
                char[] value = (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);
                int offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET);
                int count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET);
                if (offset == 0 && count == value.length)
                    // no need to copy
                    return value;
                else
                    return string.toCharArray();
            }
            
            @Override
            public String noCopyStringFromChars(char[] chars) {
                if (WRITE_TO_FINAL_FIELDS) {
                    String string = new String();
                    UNSAFE.putObject(string, STRING_VALUE_FIELD_OFFSET, chars);
                    UNSAFE.putInt(string, STRING_COUNT_FIELD_OFFSET, chars.length);
                    return string;
                } else {
                    return new String(chars);
                }
            }
        },
        UNKNOWN {
            @Override
            public char[] toCharArray(String string) {
                return string.toCharArray();
            }
            
            @Override
            public String noCopyStringFromChars(char[] chars) {
                return new String(chars);
            }
        };

        public abstract char[] toCharArray(String string);
        public abstract String noCopyStringFromChars(char[] chars);
    }

    public static StringImplementation STRING_IMPLEMENTATION = computeStringImplementation();

    private static StringImplementation computeStringImplementation() {

        if (STRING_VALUE_FIELD_OFFSET != -1L) {
            if (STRING_OFFSET_FIELD_OFFSET != -1L && STRING_COUNT_FIELD_OFFSET != -1L) {
                return StringImplementation.OFFSET;

            } else if (STRING_OFFSET_FIELD_OFFSET == -1L && STRING_COUNT_FIELD_OFFSET == -1L) {
                return StringImplementation.DIRECT_CHARS;
            } else {
                // WTF
                return StringImplementation.UNKNOWN;
            }
        } else {
            return StringImplementation.UNKNOWN;
        }
    }

    public static boolean hasUnsafe() {
        return ENABLED;
    }

    public static char[] toCharArray(final String string) {
        return STRING_IMPLEMENTATION.toCharArray(string);

    }

    public static char[] toCharArray(final CharSequence charSequence) {
        return toCharArray(charSequence.toString());
    }

    public static char[] toCharArrayFromBytes(final byte[] bytes, Charset charset) {
        return toCharArray(new String(bytes, charset != null ? charset : StandardCharsets.UTF_8));
    }

    public static String noCopyStringFromChars(final char[] chars) {
        return STRING_IMPLEMENTATION.noCopyStringFromChars(chars);
    }
}
