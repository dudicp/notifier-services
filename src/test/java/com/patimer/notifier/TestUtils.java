package com.patimer.notifier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.Assert;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;

public class TestUtils
{

    public static Date getYesterday()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Date getTomorrow()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static boolean equalsIncludeNull(Object o1, Object o2)
    {
        if(o1 == null && o2 == null)
            return true;

        if(o1 != null && o2 != null)
            return o1.equals(o2);

        return false;
    }

    public static void assertEqualsIncludeNull(Object o1, Object o2)
    {
        Assert.isTrue(equalsIncludeNull(o1, o2));
    }

    public static Principal createPrincipal(SessionPrincipal sessionPrincipal)
    {
        return new UsernamePasswordAuthenticationToken(sessionPrincipal, null /*credentials*/);
    }
}
