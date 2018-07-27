package edu.neu;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import edu.neu.controller.LoginController;

@RunWith(SpringRunner.class)
public class CloudApplicationTests {
	private MockMvc mockMvc;
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(new LoginController()).build();
	}
	@Test
	public void testLoginPage() throws Exception{
		this.mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"))
				.andDo(print());
	}
}
