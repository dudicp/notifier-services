package com.patimer.notifier.util;

import org.junit.Test;
import org.springframework.util.Assert;

public class TestTypeConverter
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertToIntFromString
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertToIntFromStringWhenValid()
    {
        // given
        String value = "100";

        // when
        int result = TypeConverter.convertToIntFromString(value);

        // then
        Assert.isTrue(result == 100);
    }

    @Test
    public void testConvertToIntFromStringWhenValidAndContainsNonNumericCharacters()
    {
        // given
        String value = "100DD";

        // when
        int result = TypeConverter.convertToIntFromString(value);

        // then
        Assert.isTrue(result == 100);
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertToIntFromStringWhenInvalid()
    {
        // given
        String value = "ddd";

        // when
        int result = TypeConverter.convertToIntFromString(value);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertToNullableIntegerFromString
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertToNullableIntegerFromStringWhenValid()
    {
        // given
        String value = "10";

        // when
        int result = TypeConverter.convertToNullableIntegerFromString(value, false);

        // then
        Assert.isTrue(result == 10);
    }

    @Test
    public void testConvertToNullableIntegerFromStringWhenValidAndContainsNonNumericCharacters()
    {
        // given
        String value = "Z10";

        // when
        int result = TypeConverter.convertToNullableIntegerFromString(value, false);

        // then
        Assert.isTrue(result == 10);
    }

    @Test
    public void testConvertToNullableIntegerFromStringWhenNull()
    {
        // given

        // when
        Integer result = TypeConverter.convertToNullableIntegerFromString(null, false);

        // then
        Assert.isNull(result);
    }

    public void testConvertToNullableIntegerFromStringWhenInvalidAndIgnoreException()
    {
        // given
        String value = "Z";

        // when
        Integer result = TypeConverter.convertToNullableIntegerFromString(value, true);

        // then
        Assert.isNull(result);
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertToNullableIntegerFromStringWhenInvalidAndDoNotIgnoreException()
    {
        // given
        String value = "Z";

        // when
        TypeConverter.convertToNullableIntegerFromString(value, false);

        // then - expected exception
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ConvertToNullableDoubleFromString
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testConvertToNullableDoubleFromStringWhenValid()
    {
        // given
        String value = "10.1";

        // when
        double result = TypeConverter.convertToNullableDoubleFromString(value, false);

        // then
        Assert.isTrue(result == 10.1);
    }

    @Test
    public void testConvertToNullableDoubleFromStringWhenValidAndContainsNonNumericCharacters()
    {
        // given
        String value = "Z10.1";

        // when
        double result = TypeConverter.convertToNullableDoubleFromString(value, false);

        // then
        Assert.isTrue(result == 10.1);
    }

    @Test
    public void testConvertToNullableDoubleFromStringWhenNull()
    {
        // given

        // when
        Double result = TypeConverter.convertToNullableDoubleFromString(null, false);

        // then
        Assert.isNull(result);
    }

    public void testConvertToNullableDoubleFromStringWhenInvalidAndIgnoreException()
    {
        // given
        String value = "Z";

        // when
        Double result = TypeConverter.convertToNullableDoubleFromString(value, true);

        // then
        Assert.isNull(result);
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertToNullableDoubleFromStringWhenInvalidAndDoNotIgnoreException()
    {
        // given
        String value = "Z";

        // when
        TypeConverter.convertToNullableDoubleFromString(value, false);

        // then - expected exception
    }


}
