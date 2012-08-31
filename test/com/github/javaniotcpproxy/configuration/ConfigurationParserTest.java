package com.github.javaniotcpproxy.configuration;

import com.github.configuration.Configuration;
import com.github.configuration.ConfigurationParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

public class ConfigurationParserTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Properties properties = new Properties();

    @Before
    public void before() {
        properties.setProperty("test1.localPort", "1");
        properties.setProperty("test1.remotePort", "2");
        properties.setProperty("test1.remoteHost", "www");

        properties.setProperty("test2.localPort", "90");
        properties.setProperty("test2.remotePort", "91");
        properties.setProperty("test2.remoteHost", "bbb");
    }

    @Test
    public void shouldGetListOnInstancesFromFile() throws URISyntaxException {
        List<Configuration> instances = ConfigurationParser.parse(properties);

        Assert.assertEquals(2, instances.size());

        Configuration test1 = instances.get(0);
        Assert.assertEquals(1, test1.getLocalPort());
        Assert.assertEquals(2, test1.getRemotePort());
        Assert.assertEquals("www", test1.getRemoteHost());

        Configuration test2 = instances.get(1);
        Assert.assertEquals(90, test2.getLocalPort());
        Assert.assertEquals(91, test2.getRemotePort());
        Assert.assertEquals("bbb", test2.getRemoteHost());
    }

    @Test
    public void shouldFailWhenCantFindLocalPort() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Please specify test1.localPort");
        properties.remove("test1.localPort");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenCantFindRemotePort() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Please specify test1.remotePort");
        properties.remove("test1.remotePort");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenCantFindRemoteHost() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Please specify test1.remoteHost");
        properties.remove("test1.remoteHost");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenCantParseToIntLocalPort() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid integer test1.localPort = X");
        properties.setProperty("test1.localPort", "X");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenCantParseToIntRemotePort() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid integer test1.remotePort = X");
        properties.setProperty("test1.remotePort", "X");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenContainsPropertyInInvalidFormat() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "Invalid property remotePort should be <proxy name>.localPort|remotePort|remoteHost");
        properties.setProperty("remotePort", "X");

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldFailWhenParseEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Please specify at least one proxy.");
        properties.clear();

        ConfigurationParser.parse(properties);
    }

    @Test
    public void shouldSuccessParseProxyNameWithDot() {
        properties.clear();
        properties.setProperty("test1.com.localPort", "1");
        properties.setProperty("test1.com.remotePort", "1");
        properties.setProperty("test1.com.remoteHost", "1");

        ConfigurationParser.parse(properties);
    }

}
