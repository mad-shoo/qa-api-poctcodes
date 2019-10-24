package www.postcodes.io.tests;

import Junit5.tags.PostCodeOne;
import TestHelper.PostCodeHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@PostCodeOne
public class PostCodeResourceOneTests {
    private HttpClient client;
    private final static Logger LOGGER = LoggerFactory.getLogger(PostCodeResourceOneTests.class);

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

    @After
    public void shutdown() {
        client.getConnectionManager().shutdown();
    }
}
