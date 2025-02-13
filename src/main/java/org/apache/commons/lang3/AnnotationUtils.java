/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.exception.UncheckedException;

public class AnnotationUtils {

    private static final ToStringStyle TO_STRING_STYLE = new ToStringStyle() {
        private static final long serialVersionUID = 1L;

        {
            setDefaultFullDetail(true);
            setArrayContentDetail(true);
            setUseClassName(true);
            setUseShortClassName(true);
            setUseIdentityHashCode(false);
            setContentStart("(");
            setContentEnd(")");
            setFieldSeparator(", ");
            setArrayStart("[");
            setArrayEnd("]");
        }

        @Override
        protected void appendDetail(final StringBuffer buffer, final String fieldName, Object value) {
            if (value instanceof Annotation) {
                value = AnnotationUtils.toString((Annotation) value);
            }
            super.appendDetail(buffer, fieldName, value);
        }

        @Override
        protected String getShortClassName(final Class<?> cls) {
            return ClassUtils.getAllInterfaces(cls).stream().filter(Annotation.class::isAssignableFrom).findFirst()
                    .map(iface -> "@" + iface.getName())
                    .orElse("");
        }
    };

    private AnnotationUtils() {
        // Prevent instantiation
    }

    private static boolean annotationArrayMemberEquals(final Annotation[] a1, final Annotation[] a2) {
        return Arrays.equals(a1, a2);
    }

    private static boolean arrayMemberEquals(final Class<?> componentType, final Object o1, final Object o2) {
        if (componentType.isAnnotation()) {
            return annotationArrayMemberEquals((Annotation[]) o1, (Annotation[]) o2);
        }
        return Arrays.deepEquals((Object[]) o1, (Object[]) o2);
    }

    private static int arrayMemberHash(final Class<?> componentType, final Object o) {
        return Arrays.deepHashCode((Object[]) o);
    }

    public static boolean equals(final Annotation a1, final Annotation a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        final Class<? extends Annotation> type1 = a1.annotationType();
        final Class<? extends Annotation> type2 = a2.annotationType();
        if (!type1.equals(type2)) {
            return false;
        }
        try {
            for (final Method m : type1.getDeclaredMethods()) {
                if (m.getParameterTypes().length == 0) {
                    final Object v1 = m.invoke(a1);
                    final Object v2 = m.invoke(a2);
                    if (!memberEquals(m.getReturnType(), v1, v2)) {
                        return false;
                    }
                }
            }
        } catch (final ReflectiveOperationException ex) {
            return false;
        }
        return true;
    }

    public static int hashCode(final Annotation a) {
        int result = 0;
        final Class<? extends Annotation> type = a.annotationType();
        for (final Method m : type.getDeclaredMethods()) {
            try {
                final Object value = m.invoke(a);
                result += hashMember(m.getName(), value);
            } catch (final ReflectiveOperationException ex) {
                throw new UncheckedException(ex);
            }
        }
        return result;
    }

    private static int hashMember(final String name, final Object value) {
        final int part1 = name.hashCode() * 127;
        return part1 ^ Arrays.deepHashCode(new Object[]{value});
    }

    public static boolean isValidAnnotationMemberType(Class<?> type) {
        if (type == null) {
            return false;
        }
        if (type.isArray()) {
            type = type.getComponentType();
        }
        return type.isPrimitive() || type.isEnum() || type.isAnnotation()
                || String.class.equals(type) || Class.class.equals(type);
    }

    private static boolean memberEquals(final Class<?> type, final Object o1, final Object o2) {
        return Arrays.deepEquals(new Object[]{o1}, new Object[]{o2});
    }

    public static String toString(final Annotation a) {
        final ToStringBuilder builder = new ToStringBuilder(a, TO_STRING_STYLE);
        for (final Method m : a.annotationType().getDeclaredMethods()) {
            if (m.getParameterTypes().length > 0) {
                continue;
            }
            try {
                builder.append(m.getName(), m.invoke(a));
            } catch (final ReflectiveOperationException ex) {
                throw new UncheckedException(ex);
            }
        }
        return builder.build();
    }
}
