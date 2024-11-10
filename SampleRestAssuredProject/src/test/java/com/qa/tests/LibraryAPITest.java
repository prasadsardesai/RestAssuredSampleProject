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

	@BeforeClass
	public void setUp() {

		ExtentReportManager.initReports();
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

		ApiUtils apiUtils = ApiUtils.init().setBaseUri("libraryapi")
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

		ApiUtils apiUtils = ApiUtils.init().setBaseUri("libraryapi")
				.setBasePath(APIEndPoint.LIBRARY_PATH + "/GetBook.php").withQueryParam("AuthorName", "Donald Trump")
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
	 * API: Library API
	 * Scenario:TC003_addBookScenario
	 * 
	 * Description: Verify POST / add-book operation with valid data input
	 * 
	 */



	@Test
	public void TC003_addBook_Scenario() {

		ExtentReportManager.startTest("Test Library API: POST /add-book operation with invalid data");

		Library libraryObj= new Library("My Gujrati Book","acv0","88654","Vaibhavi Sardesai");
		String request= JsonPathParser.convertToJson(libraryObj);
		ApiUtils apiUtils = ApiUtils.init()
				.setBaseUri("libraryapi")
				.setBasePath(APIEndPoint.LIBRARY_PATH + "/GetBook.php")
				.withBody(request)
				.post();
		Response response = apiUtils.getResponse();



		try {
			

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
