package com.patimer.notifier.util;


public class TypeConverter
{
    public static int convertToIntFromString(String valueAsString)
    {
        valueAsString = removeNonNumericCharacters(valueAsString);
        return Integer.parseInt(valueAsString);
    }

    public static Integer convertToNullableIntegerFromString(String valueAsString, boolean ignoreException)
    {
        if(valueAsString == null || valueAsString.isEmpty())
            return null;

        try
        {
            valueAsString = removeNonNumericCharacters(valueAsString);
            return Integer.parseInt(valueAsString);
        }
        catch(NumberFormatException e)
        {
            // ignore and return null
            if(ignoreException)
                return null;
            else
                throw e;
        }
    }

    public static Double convertToNullableDoubleFromString(String valueAsString, boolean ignoreException)
    {
        if(valueAsString == null || valueAsString.isEmpty())
            return null;

        try
        {
            valueAsString = removeNonNumericCharacters(valueAsString);
            return Double.parseDouble(valueAsString);
        }
        catch(NumberFormatException e)
        {
            // ignore and return null
            if(ignoreException)
                return null;
            else
                throw e;
        }
    }

    public static String removeNonNumericCharacters(String value)
    {
        return value.replaceAll("[^\\d.]", ""); // remove all non-numeric characters
    }
}
