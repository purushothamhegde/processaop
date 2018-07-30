package com.philips.processaop.model;

import java.lang.reflect.Method;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * @author Richard Kenyon
 *
 */
public class LoggingObject {

    /**
     * Uses reflection to find all method names starting with "get" to build a string representation
     * of the object. Dates will be formatted to dd/MM/YYYY format. Override this method if any
     * special formatting is required.
     *
     * @return  A String representation of this object.
     */
    public String toString() {

        SimpleDateFormat sdf       = new SimpleDateFormat("dd/MM/yyyy");
        StringBuffer     sb        = new StringBuffer(this.getClass().getName() + "[");
        Method           methods[] = this.getClass().getMethods();
        boolean          addComma  = false;

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];

            if (method.getName().startsWith("get")) {
                String fieldName = method.getName().substring(3);

                if (addComma) {
                    sb.append(", ");
                }

                try {

                    Object obj   = method.invoke(this, (Object[]) null);
                    String value = "";

                    if (obj instanceof Date) {
                        value = sdf.format((Date) obj);
                    } else {

                        if (obj == null) {
                            value = "NULL";
                        } else {
                            value = obj.toString();
                        }
                    }

                    sb.append(fieldName + "=" + value);

                    addComma = true;

                } catch (Exception e) {

                    sb.append(fieldName + "=Exception");
                }
            }
        }

        sb.append("]");

        return (sb.toString());
    }
}
