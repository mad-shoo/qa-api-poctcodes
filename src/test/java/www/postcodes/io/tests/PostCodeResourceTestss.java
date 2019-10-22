package www.postcodes.io.tests;

import Junit5.tags.PostCodeTwo;
import TestHelper.PostCodeHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import www.postcodes.io.config.TestConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@PostCodeTwo
public class PostCodeResourceTestss {
    private HttpClient client;
    private final static Logger LOGGER = LoggerFactory.getLogger(PostCodeResourceTestss.class);

    @Before
    public void loadConfig() {
        TestConfig.loadConfig();
        client = HttpClientBuilder.create().build();
    }

    @Test
    public void getPostCodesByPathParameterValidPostCodesWithLowerCaseTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByPathParameterRequest("LowerCase", FALSE);
        getPostcodeByPathParameterValidations(getResponse, FALSE);
    }

    @Test
    public void getPostCodesByPathParameterValidPostCodesWithUpperCaseTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByPathParameterRequest("UpperCase", FALSE);
        getPostcodeByPathParameterValidations(getResponse, FALSE);
    }

    @Test
    public void getPostCodesByPathParameterInvalidPostCodesWithUpperCaseTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByPathParameterRequest("UpperCase", TRUE);
        getPostcodeByPathParameterValidations(getResponse, TRUE);
    }

    @Test
    public void getPostCodesByPathParameterInvalidPostCodesWithLowerCaseTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByPathParameterRequest("LowerCase", TRUE);
        getPostcodeByPathParameterValidations(getResponse, TRUE);
    }

    @Test
    public void postValidPostCodesTest() throws IOException, ParseException {
        HttpResponse postResponse = postPostcodeRequest(FALSE);
        postPostCodesValidations(postResponse, FALSE);
    }

    @Test
    public void postInvalidPostCodesTest() throws IOException, ParseException {
        HttpResponse postResponse = postPostcodeRequest(TRUE);
        postPostCodesValidations(postResponse, TRUE);
    }

    @Test
    public void getPostCodesByQueryParameterValidPostCodeTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByQueryParameterRequest(FALSE);
        getPostCodesByQueryParameterValidations(getResponse, FALSE);
    }

    @Test
    public void getPostCodesByQueryParameterInvalidPostCodeTest() throws IOException, ParseException, URISyntaxException {
        HttpResponse getResponse = getPostcodeByQueryParameterRequest(TRUE);
        getPostCodesByQueryParameterValidations(getResponse, TRUE);
    }

    private HttpResponse getPostcodeByPathParameterRequest(String caseFormat, Boolean invalidPostCode) throws IOException, URISyntaxException {
        String postCode;
        if (Boolean.TRUE.equals(invalidPostCode)) {
            postCode = TestConfig.getConfigElement("invalidPostCodeOne");
        } else
            postCode = TestConfig.getConfigElement("path_parameter_postcode");
        Assert.assertNotNull(postCode);
        Assert.assertNotNull(TestConfig.getURI());
        HttpResponse resp;
        if (caseFormat.equalsIgnoreCase("LowerCase")) {
            resp = PostCodeHelper.getRequest(TestConfig.getURI(), postCode.toLowerCase(), FALSE);
        } else
            resp = PostCodeHelper.getRequest(TestConfig.getURI(), postCode.toUpperCase(), FALSE);
        Assert.assertNotNull(resp);
        return resp;
    }

    private void getPostcodeByPathParameterValidations(HttpResponse response, Boolean invalidPostCode) throws ParseException, IOException {
        HttpEntity httpEntity = response.getEntity();
        String respOutput = EntityUtils.toString(httpEntity);
        LOGGER.info(String.valueOf(respOutput));
        Object respJson = new JSONParser().parse(respOutput);
        JSONObject jsonObject = (JSONObject) respJson;
        if (Boolean.TRUE.equals(invalidPostCode)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
            Assert.assertNull(jsonObject.get("result"));
            Assert.assertEquals("Invalid postcode", jsonObject.get("error"));
        } else {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Map result = ((Map) jsonObject.get("result"));
            Assert.assertEquals(TestConfig.getConfigElement("postCodeOne"), result.get("postcode"));
        }
    }

    private void getPostcodeByPathParameterValidationss(HttpResponse response, Boolean invalidPostCode) throws ParseException, IOException {
        HttpEntity httpEntity = response.getEntity();
        String respOutput = EntityUtils.toString(httpEntity);
        LOGGER.info(String.valueOf(respOutput));
        Object respJson = new JSONParser().parse(respOutput);
        JSONObject jsonObject = (JSONObject) respJson;
        if (Boolean.TRUE.equals(invalidPostCode)) {
            Assert.assertEquals(404, response.getStatusLine().getStatusCode());
            Assert.assertNull(jsonObject.get("result"));
            Assert.assertEquals("Invalid postcode", jsonObject.get("error"));
        } else {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            Map result = ((Map) jsonObject.get("result"));
            Assert.assertEquals(TestConfig.getConfigElement("postCodeOne"), result.get("postcode"));
        }
    }

   /* private HttpResponse postPostcodeRequest(Boolean invalidPostCode) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(System.getProperty("user.dir") + File.separator + "src/test/Resources/Post_postcodes.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray postcodes = new JSONArray();
        if (Boolean.TRUE.equals(invalidPostCode)) {
            postcodes.add(TestConfig.getConfigElement("invalidPostCodeOne"));
            postcodes.add(TestConfig.getConfigElement("invalidPostCodeTwo"));
        } else {
            postcodes.add(TestConfig.getConfigElement("postCodeOne"));
            postcodes.add(TestConfig.getConfigElement("postCodeTwo"));
        }
        jsonObject.put("postcodes", postcodes);
        Assert.assertNotNull(jsonObject);
        Assert.assertNotNull(TestConfig.getURI());
        HttpResponse resp = PostCodeHelper.postRequest(TestConfig.getURI(), jsonObject);
        Assert.assertNotNull(resp);
        return resp;
    }*/

    private void postPostCodesValidations(HttpResponse response, Boolean invalidPostCode) throws IOException, ParseException {
        Map result = (Map) getJsonResponse(response).get(0);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        if (Boolean.TRUE.equals(invalidPostCode)) {
            Assert.assertEquals(TestConfig.getConfigElement("invalidPostCodeOne"), result.get("query"));
            Assert.assertNull(result.get("result"));
            result = (Map) getJsonResponse(response).get(1);
            Assert.assertEquals(TestConfig.getConfigElement("invalidPostCodeTwo"), result.get("query"));
            Assert.assertNull(result.get("result"));
        } else {
            Assert.assertEquals(TestConfig.getConfigElement("postCodeOne"), result.get("query"));
            result = (Map) getJsonResponse(response).get(1);;
            Assert.assertEquals(TestConfig.getConfigElement("postCodeTwo"), result.get("query"));
        }
    }

    private HttpResponse getPostcodeByQueryParameterRequest(Boolean invalidPostCode) throws IOException, URISyntaxException {
        String postCode;
        if (Boolean.TRUE.equals(invalidPostCode)) {
            postCode = TestConfig.getConfigElement("invalid_query_parameter_postcode");
        } else
            postCode = TestConfig.getConfigElement("postCodeOne");
        Assert.assertNotNull(postCode);
        Assert.assertNotNull(TestConfig.getURI());
        HttpResponse resp = PostCodeHelper.getRequest(null, postCode, TRUE);
        LOGGER.info(String.valueOf(resp));
        Assert.assertNotNull(resp);
        return resp;
    }

    public void getPostCodesByQueryParameterValidations(HttpResponse response, Boolean invalidPostCode) throws IOException, ParseException {
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Map result = null;
        if (Boolean.TRUE.equals(invalidPostCode)) {
            Assert.assertNull(EntityUtils.toString(response.getEntity()));
        } else {
            result = (Map) getJsonResponse(response).get(0);
            Assert.assertEquals(TestConfig.getConfigElement("postCodeOne"), result.get("postcode"));
        }
    }

    public JSONArray getJsonResponse(HttpResponse response) throws IOException, ParseException {
        String respOutput = EntityUtils.toString(response.getEntity());
        LOGGER.info(String.valueOf(respOutput));
        Object respJson = new JSONParser().parse(respOutput);
        JSONObject jsonObject = (JSONObject) respJson;
        return (JSONArray) jsonObject.get("result");
    }

    public JSONObject getJsonResponse(HttpResponse response, String method) throws IOException, ParseException {
        String respOutput = EntityUtils.toString(response.getEntity());
        LOGGER.info(String.valueOf(respOutput));
        Object respJson = new JSONParser().parse(respOutput);
        JSONObject jsonObject = (JSONObject) respJson;
        if (method.equalsIgnoreCase("post")) {
            return jsonObject;
        } else
        return (JSONObject) jsonObject.get("result");
    }

    private HttpResponse postPostcodeRequest(Boolean invalidPostCode) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(System.getProperty("user.dir") + File.separator + "src/test/Resources/Post_postcodess.json"));
        JSONObject jsonObject = (JSONObject) obj;
        Assert.assertNotNull(jsonObject);
        Assert.assertNotNull(TestConfig.getURI());
        HttpResponse resp = PostCodeHelper.postRequest(TestConfig.getURI(), jsonObject);
        Assert.assertNotNull(resp);
        return resp;
    }


    @After
    public void shutdown() {
        client.getConnectionManager().shutdown();
    }
}
