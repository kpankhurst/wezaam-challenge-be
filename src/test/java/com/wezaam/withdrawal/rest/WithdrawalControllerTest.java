package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.Application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@Sql(scripts = {"/clearDB.sql", "/import.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class WithdrawalControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	void createWithdrawal() throws Exception{
		/* http://localhost:7070/create-withdrawals?userId=1&paymentMethodId=4&amount=100&executeAt=2017-02-03T10:37:30.00Z
		 * {
			    "id": 1,
			    "transactionId": null,
			    "amount": 100.0,
			    "createdAt": "2022-07-20T07:43:34.200256700Z",
			    "executeAt": "2017-02-03T10:37:30Z",
			    "userId": 1,
			    "paymentMethodId": 4,
			    "retries": 0,
			    "status": "PENDING"
			}
		 */
		
		mvc.perform(post("/create-withdrawals/")
				.param("userId", "1")
				.param("paymentMethodId", "4")
				.param("amount", "100")
				.param("executeAt", "2017-02-03T10:37:30.00Z")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))

				.andExpect(MockMvcResultMatchers.status().isOk())

	            .andExpect(jsonPath("$.id").exists())
	            .andExpect(jsonPath("$.amount").exists())
	            .andExpect(jsonPath("$.createdAt").exists())
	            .andExpect(jsonPath("$.executeAt").exists())
	            .andExpect(jsonPath("$.userId").exists())
	            .andExpect(jsonPath("$.paymentMethodId").exists())
	            .andExpect(jsonPath("$.retries").exists())
	            .andExpect(jsonPath("$.status").exists());
	}

	@Test
	void exceedWithdrawalAmount() throws Exception{
		/* http://localhost:7070/create-withdrawals?userId=1&paymentMethodId=4&amount=1000&executeAt=2017-02-03T10:37:30.00Z
		 * Will exceed withawal amount
		 */
		
		mvc.perform(post("/create-withdrawals/")
				.param("userId", "1")
				.param("paymentMethodId", "4")
				.param("amount", "1000")
				.param("executeAt", "2017-02-03T10:37:30.00Z")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))

				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void getAllWithdrawals() throws Exception{
		mvc.perform(post("/create-withdrawals/")
				.param("userId", "1")
				.param("paymentMethodId", "4")
				.param("amount", "100")
				.param("executeAt", "2017-02-03T10:37:30.00Z")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		mvc.perform(get("/find-all-withdrawals/")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))

				.andExpect(MockMvcResultMatchers.status().isOk())

	            .andExpect(jsonPath("$").isArray())
	            
	            .andExpect(jsonPath("$[*].id").exists())
	            .andExpect(jsonPath("$[*].transactionId").exists())
	            .andExpect(jsonPath("$[*].amount").isArray())
	            .andExpect(jsonPath("$[*].createdAt").isArray())
	            .andExpect(jsonPath("$[*].executeAt").isArray())
	            .andExpect(jsonPath("$[*].userId").isArray())
	            .andExpect(jsonPath("$[*].paymentMethodId").isArray())
	            .andExpect(jsonPath("$[*].retries").isArray())
	            .andExpect(jsonPath("$[*].status").isArray());
	}	
}