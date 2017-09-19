package com.newrelic.metrics.publish.configuration;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void testGetUntypedDefaultValue() {
        Config.init(Collections.<String, Object>emptyMap());

        assertEquals("log_name", Config.getValue("test_property", "log_name"));
        assertTrue(Config.getValue("test_property", true));
    }

    @Test
    public void testGetUntypedValue() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put("testProp1", "testValue");
        config.put("testProp2", false);
        config.put("testProp3", 5);
        config.put("testProp4", 10.0f);

        Config.init(config);

        final String testValue1 = Config.getValue("testProp1");
        final boolean testValue2 = Config.getValue("testProp2");
        final int testValue3 = Config.getValue("testProp3");
        final float testValue4 = Config.getValue("testProp4");
        final String testValue5 = Config.getValue("testProp5");

        assertEquals("testValue", testValue1);
        assertFalse(testValue2);
        assertEquals(5, testValue3);
        assertEquals(10.0, testValue4, 0.0001);
        assertNull(testValue5);
    }

    @Test
    public void testGetTypedValue() {
        final Map<String, Object> config = new HashMap<String, Object>();
        config.put("testProp1", "testValue");
        config.put("testProp2", false);
        config.put("testProp3", 5);
        config.put("testProp4", 10.0f);

        Config.init(config);

        assertEquals("testValue", Config.<String>getValue("testProp1"));
        assertFalse(Config.<Boolean>getValue("testProp2"));
        assertEquals(5, Config.<Integer>getValue("testProp3").intValue());
        assertEquals(10.0, Config.<Float>getValue("testProp4").floatValue(), 0.0001);
        assertNull(Config.<String>getValue("testProp5"));
    }

    @Test
    public void testGetMapValue() {
        final Map<String, Object> config = new HashMap<String, Object>();
        final Map<String, Object> innerMap = new HashMap<String, Object>();
        innerMap.put("key", true);
        config.put("inner_map", innerMap);

        Config.init(config);

        final Map<String, Object> innerValue = Config.<Map<String, Object>>getValue("inner_map");
        assertTrue((Boolean) innerValue.get("key"));
    }

    @Test
    public void testGetConfigDirectory() {
        assertEquals("config", Config.getConfigDirectory());

        System.setProperty("newrelic.platform.config.dir", "config2");
        assertEquals("config2", Config.getConfigDirectory());
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadWithStringThrowsException() throws IOException {
        Config.load("bad_path");
    }

    @Test
    public void testOptionalLoadWithStringDoesNotThrowException() throws IOException {
        Config.load("bad_path", false);
    }

    @Test(expected = IOException.class)
    public void testLoadWithFileThrowsException() throws IOException {
        final File file = new File("bad_path");
        Config.load(file);
    }

    @Test(expected = IOException.class)
    public void testLoadWithInvalidJsonThrowsException() throws IOException, URISyntaxException {
        final File file = getFile("bad_config.json");
        Config.load(file);
    }

    @Test
    public void testLoadWithValidJson() throws IOException, URISyntaxException {
        Config.init(new HashMap<String, Object>());
        Config.load(getFile("config.json"));

        assertEquals("http://ENDPOINT", Config.getValue("end_point"));
        assertEquals("LICENSE_KEY", Config.getValue("license_key"));
        assertEquals(10.3d, Config.getValue("float_test"), 0);
        assertEquals(true, Config.getValue("enabled"));
        assertEquals("info", Config.getValue("log_level"));
        assertEquals("logs/newrelic_plugin_installer.log", Config.getValue("log_file_name"));
        assertEquals(25L, Config.getValue("log_max_size_in_mb"), 0);

        final Map<String, Object> mapProp = Config.getValue("map_prop");
        assertEquals(1L, mapProp.get("one"));
        assertFalse((Boolean) mapProp.get("two"));

        final List<Map<String, Object>> listProp = Config.getValue("list_prop");
        final Map<String, Object> three = (Map<String, Object>) listProp.get(0);
        assertEquals(3L, three.get("three"));
        final Map<String, Object> four = (Map<String, Object>) listProp.get(1);
        @SuppressWarnings("unchecked") final Map<String, Object> innerMap = (Map<String, Object>) four.get("four");
        assertTrue((Boolean) innerMap.get("inner_value"));
    }

    @Test
    public void testGetSdkVersion() {
        final String sdkVersion = Config.getSdkVersion();
        assertNotNull(sdkVersion);
    }

    @Test
    public void testLoadTwoJsonFiles() throws IOException, URISyntaxException {
        Config.init(new HashMap<String, Object>());
        Config.load(getFile("config.json"));
        Config.load(getFile("config2.json"));

        assertEquals("http://overridden-endpoint", Config.getValue("end_point"));
        assertEquals("LICENSE_KEY", Config.getValue("license_key"));
        assertEquals("new", Config.getValue("new_prop"));
    }

    private File getFile(final String fileName) throws URISyntaxException {
        return new File(ConfigTest.class.getResource(fileName).toURI());
    }
}
