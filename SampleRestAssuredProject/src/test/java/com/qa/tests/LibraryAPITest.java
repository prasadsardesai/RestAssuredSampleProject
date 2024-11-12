package com.qa.tests;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.qa.constants.APIEndPoint;
import com.qa.extentreport.ExtentReportManager;
import com.qa.jsonparser.JsonPathParser;
import com.qa.pojo.Library;
import com.qa.requestspecifications.ApiUtils;

import io.restassured.response.Response;

public class LibraryAPITest {
	
	 private ApiUtils apiUtils;
	private static final String BASE_URI = "libraryapi";

	@BeforeClass
	public void setUp() {

		ExtentReportManager.initReports();
		apiUtils = ApiUtils.init().setBaseUri(BASE_URI);
	}

	/**
	 * API: Library API Scenario:TC001_getBookByAuthorName_positiveScenario
	 * 
	 * Description: Verify GET / get-book-by-authorname operation with valid data
	 * input
	 * 
	 */

	@Test
	public void TC001_getBookByAuthorName_positiveScenario() {

		ExtentReportManager.startTest("Test Library API: Get /book-by-authorname operation with valid data");

		apiUtils
				.setBasePath(APIEndPoint.LIBRARY_PATH + "/GetBook.php")
				.withQueryParam("AuthorName", "Vaibhavi Sardesai").get("");
		Response response = apiUtils.getResponse();

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("AuthorName", "Vaibhavi Sardesai");

		ExtentReportManager.logRequest(apiUtils.getBaseUri("libraryapi"), APIEndPoint.LIBRARY_PATH + "/GetBook.php",
				queryParams);

		ExtentReportManager.logInfo("Response Status Code: " + response.statusCode());

		ExtentReportManager.logResponse(response);

		try {

			// Verify status code
			Assert.assertNotNull(response, "Response should not be Null");
			Assert.assertEquals(response.getStatusCode(), 200, "Status mismatched.");
			ExtentReportManager.logPass("Test passed: Status code is 200 as expected.");

			// Verify the values in response are as expected
			String actualBookName = JsonPathParser.getStringValue(response.asString(), "[0].book_name");
			String actualisbn = JsonPathParser.getStringValue(response.asString(), "[0].isbn");
			String actualaisle = JsonPathParser.getStringValue(response.asString(), "[0].aisle");

			Assert.assertEquals(actualBookName, "My Gujrati Book",
					"Actual Book Name does not match with the expected book");
			Assert.assertEquals(actualisbn, "acv0", "Actual isbn value does not match with the expected isbn value");
			Assert.assertEquals(actualaisle, "88654",
					"Actual aisle value does not match with the expected aisle value");
			ExtentReportManager.logPass("Test passed: Book details verified as expected.");
		} catch (AssertionError e) {
			ExtentReportManager.logFail("Test failed: " + e.getMessage());
			throw e;
		}

	}

	/**
	 * API: Library API Scenario:TC002_getBookByAuthorName_NegativeScenario
	 * 
	 * Description: Verify GET / get-book-by-authorname operation with invalid data
	 * input
	 * 
	 */

	@Test
	public void TC002_getBookByAuthorName_NegativeScenario() {

		ExtentReportManager.startTest("Test Library API: Get /book-by-authorname operation with invalid data");

		apiUtils.setBasePath(APIEndPoint.LIBRARY_PATH + "/GetBook.php")
		.withQueryParam("AuthorName", "Donald Trump")
		.get("");
		Response response = apiUtils.getResponse();

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("AuthorName", "Donald Trump");

		ExtentReportManager.logRequest(apiUtils.getBaseUri("libraryapi"), APIEndPoint.LIBRARY_PATH + "/GetBook.php",
				queryParams);

		ExtentReportManager.logInfo("Response Status Code: " + response.statusCode());

		ExtentReportManager.logResponse(response);

		try {

			// Verify status code
			Assert.assertNotNull(response, "Response should not be Null");
			Assert.assertEquals(response.getStatusCode(), 404, "Status mismatched.");
			ExtentReportManager.logPass("Test passed: Status code is 404 as expected.");

			// Verify the values in response are as expected
			String actualMsg = JsonPathParser.getStringValue(response.asString(), "msg");

			Assert.assertEquals(actualMsg, "The book by requested bookid / author name does not exists!",
					"Actual Message does not match with the expected Message");
			ExtentReportManager.logPass("Test passed: Message content verified as expected.");
		} catch (AssertionError e) {
			ExtentReportManager.logFail("Test failed: " + e.getMessage());
			throw e;
		}

	}

	/**
	 * API: Library API Scenario:TC003_addBookScenario
	 * 
	 * Description: Verify POST / add-book operation with valid data input
	 * 
	 */

	@Test
	public void TC003_addBook_Scenario() {

		ExtentReportManager.startTest("Test Library API: POST /add-book operation with valid data");

		Library libraryObj = new Library("My Test Book", "auv01", "87948", "C B Sardesai");
		String request = JsonPathParser.convertToJson(libraryObj);
		apiUtils.setBasePath(APIEndPoint.LIBRARY_PATH + "/Addbook.php")
		.withBody(request).post();
		Response response = apiUtils.getResponse();
		ExtentReportManager.logRequest(apiUtils.getBaseUri("libraryapi"), APIEndPoint.LIBRARY_PATH + "/Addbook.php",
				JsonPathParser.prettyPrint(request));
		ExtentReportManager.logInfo("Response Status Code: " + response.statusCode());

		ExtentReportManager.logResponse(response);

		try {

			// Verify status code
			Assert.assertNotNull(response, "Response should not be Null");
			Assert.assertEquals(response.getStatusCode(), 200, "Status mismatched.");
			ExtentReportManager.logPass("Test passed: Status code is 200 as expected.");

			// Verify the values in response are as expected
			String actualMsg = JsonPathParser.getStringValue(response.asString(), "Msg");

			Assert.assertEquals(actualMsg, "successfully added",
					"Actual Message does not match with the expected Message");

			String actualId = JsonPathParser.getStringValue(response.asString(), "ID");
			Assert.assertNotNull(actualId, "ID field should not be Null");
			
			String deleteReq = String.format("{ \"ID\": \"%s\" }", actualId);
			ApiUtils apiUtilss = ApiUtils.init().setBaseUri("libraryapi")
					.setBasePath(APIEndPoint.LIBRARY_PATH + "/DeleteBook.php").withBody(deleteReq).post();
			ExtentReportManager.logPass("Test passed: Message content verified as expected.");
		} catch (AssertionError e) {
			ExtentReportManager.logFail("Test failed: " + e.getMessage());
			throw e;
		}

	}


	
	/**
	 * API: Library API Scenario:TC004_deleteBookScenario
	 * 
	 * Description: Verify POST / delete-book operation with valid data input
	 * 
	 */

	@Test
	public void TC004_deleteBook_Scenario() {
		
		String actualId="";

		ExtentReportManager.startTest("Test Library API: POST /delete-book operation with valid data");

		Library libraryObj = new Library("My Test Book", "auv01", "87948", "C B Sardesai");
		String request = JsonPathParser.convertToJson(libraryObj);
		 apiUtils.setBasePath(APIEndPoint.LIBRARY_PATH + "/Addbook.php")
		 .withBody(request).post();
		Response response = apiUtils.getResponse();
		ExtentReportManager.logRequest(apiUtils.getBaseUri("libraryapi"), APIEndPoint.LIBRARY_PATH + "/Addbook.php",
				JsonPathParser.prettyPrint(request));
		ExtentReportManager.logInfo("Response Status Code: " + response.statusCode());

		ExtentReportManager.logResponse(response);

		try {

			// Verify status code
			Assert.assertNotNull(response, "Response should not be Null");
			Assert.assertEquals(response.getStatusCode(), 200, "Status mismatched.");
			ExtentReportManager.logPass("Test passed: Status code is 200 as expected.");

			// Verify the values in response are as expected
			String actualMsg = JsonPathParser.getStringValue(response.asString(), "Msg");

			Assert.assertEquals(actualMsg, "successfully added",
					"Actual Message does not match with the expected Message");

			 actualId = JsonPathParser.getStringValue(response.asString(), "ID");
			Assert.assertNotNull(actualId, "ID field should not be Null");
			
			
			//ExtentReportManager.logPass("Test passed: Message content verified as expected.");
		} catch (AssertionError e) {
			ExtentReportManager.logFail("Test failed: " + e.getMessage());
			throw e;
		}
		
		
		String deleteReq = String.format("{ \"ID\": \"%s\" }", actualId);
			ApiUtils apiUtilss = ApiUtils.init().setBaseUri("libraryapi")
					.setBasePath(APIEndPoint.LIBRARY_PATH + "/DeleteBook.php").withBody(deleteReq).post();
					
		Response response2 = apiUtilss.getResponse();
		ExtentReportManager.logRequest(apiUtilss.getBaseUri("libraryapi"), APIEndPoint.LIBRARY_PATH + "/DeleteBook.php",
				JsonPathParser.prettyPrint(deleteReq));
		ExtentReportManager.logInfo("Response Status Code: " + response2.statusCode());

		ExtentReportManager.logResponse(response2);
		
			try {

			// Verify status code
			Assert.assertNotNull(response2, "Response should not be Null");
			Assert.assertEquals(response2.getStatusCode(), 200, "Status mismatched.");
			ExtentReportManager.logPass("Test passed: Status code is 200 as expected.");

			// Verify the values in response are as expected
			String actualMsg2 = JsonPathParser.getStringValue(response2.asString(), "msg");

			Assert.assertEquals(actualMsg2, "book is successfully deleted",
					"Actual Message does not match with the expected Message");
			
			
			ExtentReportManager.logPass("Test passed: Message content verified as expected.");
		} catch (AssertionError e) {
			ExtentReportManager.logFail("Test failed: " + e.getMessage());
			throw e;
		}

	}
	
	@AfterClass
	public void tearDown() {

		ExtentReportManager.endTest();
	}
	

}
