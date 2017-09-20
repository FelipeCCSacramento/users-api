package br.com.felipeccsacramento.documentation;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.felipeccsacramento.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerDocumentation {

	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private FieldDescriptor[] getUserFields() {
		return new FieldDescriptor[] {
			fieldWithPath("id").description("Id do usuário"),
			fieldWithPath("cpf").description("CPF do usuário"),
			fieldWithPath("firstName").description("Nome usuário"),
			fieldWithPath("lastName").description("Sobrenome usuário"),
			fieldWithPath("birthDate").description("Data de nascimento do usuário"),
			fieldWithPath("address").description("Endereço do usuário"),
			fieldWithPath("phone").description("Telefone do usuário")
		};
	}

	@Before
	public void setup() {
		
		mockMvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)).build();
		
	}

	@Test
	public void test01_givenContext_whenServletContext_thenItProvidesUserController() {
		ServletContext servletContext = context.getServletContext();

		Assert.assertNotNull(servletContext);
		Assert.assertTrue(servletContext instanceof MockServletContext);
		Assert.assertNotNull(context.getBean("userController"));
	}

	@Test
	public void test02_givenAddFirstUserURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento")
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();
		
		FieldDescriptor[] requestFields = getUserFields();
		requestFields[0].ignored();
		
		String payload = mapper.writeValueAsString(firstUser);
		mockMvc.perform(post("/users").content(payload).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.header().string("location", "http://localhost:8080/users/1"))
			.andDo(document("create-user",
				requestFields(requestFields),
				responseHeaders(headerWithName("location").description("URI do recurso criado com Id do usuário"))
			));
	}

	@Test
	public void test03_givenGetFirstUserAddedURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento").id(1L)
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();
		
		mockMvc.perform(get("/users/{id}", firstUser.getId()).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.id").value(firstUser.getId()))
			.andExpect(jsonPath("$.cpf").value(firstUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$.firstName").value(firstUser.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(firstUser.getLastName()))
			.andExpect(jsonPath("$.birthDate").value(firstUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$.address").value(firstUser.getAddress()))
			.andExpect(jsonPath("$.phone").value(firstUser.getPhone().replaceAll("\\D", "")))
			.andDo(document("get-user", 
				pathParameters(parameterWithName("id").description("Id do usuário")),
				responseFields(getUserFields())
			));

	}
	
	@Test
	public void test04_givenGetUserByCpfURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento").id(1L)
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();
		
		mockMvc.perform(get("/users/cpf/{cpf}", firstUser.getCpf().replaceAll("\\D", "")).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.id").value(firstUser.getId()))
			.andExpect(jsonPath("$.cpf").value(firstUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$.firstName").value(firstUser.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(firstUser.getLastName()))
			.andExpect(jsonPath("$.birthDate").value(firstUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$.address").value(firstUser.getAddress()))
			.andExpect(jsonPath("$.phone").value(firstUser.getPhone().replaceAll("\\D", "")))
			.andDo(document("get-user-by-cpf", 
				pathParameters(parameterWithName("cpf").description("CPF do usuário")),
				responseFields(getUserFields())
			));

	}
	
	@Test
	public void test05_givenAddSecondURI_whenMockMVC_thenResponseOK() throws Exception {
		User secondUser = new User.UserBuilder("111.111.111-11", "Teste", "Nascimento")
				.birthDate(LocalDate.of(1981, 10, 9)).address("Rua de Teste, 555 - apto 11").phone("(91) 88888-8888")
				.build();
		
		String payload = mapper.writeValueAsString(secondUser);
		mockMvc.perform(post("/users").content(payload).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.header().string("location", "http://localhost:8080/users/2"));
		secondUser.setId(2L);
	}
	
	@Test
	public void test06_givenGetSecondUserAddedURI_whenMockMVC_thenResponseOK() throws Exception {
		User secondUser = new User.UserBuilder("111.111.111-11", "Teste", "Nascimento").id(2L)
				.birthDate(LocalDate.of(1981, 10, 9)).address("Rua de Teste, 555 - apto 11").phone("(91) 88888-8888")
				.build();
		
		
		mockMvc.perform(get("/users/{id}", secondUser.getId()).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.id").value(secondUser.getId()))
			.andExpect(jsonPath("$.cpf").value(secondUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$.firstName").value(secondUser.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(secondUser.getLastName()))
			.andExpect(jsonPath("$.birthDate").value(secondUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$.address").value(secondUser.getAddress()))
			.andExpect(jsonPath("$.phone").value(secondUser.getPhone().replaceAll("\\D", "")));

	}
	
	@Test
	public void test07_givenGetUsersListURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento").id(1L)
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();
		User secondUser = new User.UserBuilder("111.111.111-11", "Teste", "Nascimento").id(2L)
				.birthDate(LocalDate.of(1981, 10, 9)).address("Rua de Teste, 555 - apto 11").phone("(91) 88888-8888")
				.build();
		
		mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			
			.andExpect(jsonPath("$[0].id").value(firstUser.getId()))
			.andExpect(jsonPath("$[0].cpf").value(firstUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$[0].firstName").value(firstUser.getFirstName()))
			.andExpect(jsonPath("$[0].lastName").value(firstUser.getLastName()))
			.andExpect(jsonPath("$[0].birthDate").value(firstUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$[0].address").value(firstUser.getAddress()))
			.andExpect(jsonPath("$[0].phone").value(firstUser.getPhone().replaceAll("\\D", "")))
			
			.andExpect(jsonPath("$[1].id").value(secondUser.getId()))
			.andExpect(jsonPath("$[1].cpf").value(secondUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$[1].firstName").value(secondUser.getFirstName()))
			.andExpect(jsonPath("$[1].lastName").value(secondUser.getLastName()))
			.andExpect(jsonPath("$[1].birthDate").value(secondUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$[1].address").value(secondUser.getAddress()))
			.andExpect(jsonPath("$[1].phone").value(secondUser.getPhone().replaceAll("\\D", "")))
			.andDo(document("get-users-list", 
				responseFields(fieldWithPath("[]").description("Array de usuários"))
					.andWithPrefix("[].",getUserFields())
			));

	}
	
	@Test
	public void test08_givenPatchFirstUserURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento").id(1L)
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();

		Map<String, String> payload = new HashMap<>();
		payload.put("firstName", "Patched ".concat(firstUser.getFirstName()));
		payload.put("lastName", "Patched ".concat(firstUser.getLastName()));
		
		mockMvc.perform(patch("/users/{id}", firstUser.getId()).content(mapper.writeValueAsString(payload))
			.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.id").value(firstUser.getId()))
			.andExpect(jsonPath("$.cpf").value(firstUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$.firstName").value(payload.get("firstName")))
			.andExpect(jsonPath("$.lastName").value(payload.get("lastName")))
			.andExpect(jsonPath("$.birthDate").value(firstUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$.address").value(firstUser.getAddress()))
			.andExpect(jsonPath("$.phone").value(firstUser.getPhone().replaceAll("\\D", "")))
			.andDo(document("patch-user", 
				pathParameters(parameterWithName("id").description("Id do usuário")),
				requestFields(
					fieldWithPath("firstName").description("Nome do usuário"),
					fieldWithPath("lastName").description("Sobrenome do usuário")
				),
				responseFields(getUserFields())
			));
		
	}
	
	@Test
	public void test09_givenUpdateFirstUserURI_whenMockMVC_thenResponseOK() throws Exception {
		User firstUser = new User.UserBuilder("000.000.000-00", "Felipe", "Sacramento").id(1L)
				.birthDate(LocalDate.of(1994, 5, 6)).address("Rua de Teste, 999 - apto 77").phone("(11) 99999-9999")
				.build();
	
		mockMvc.perform(put("/users/{id}", firstUser.getId()).content(mapper.writeValueAsString(firstUser))
			.contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$.id").value(firstUser.getId()))
			.andExpect(jsonPath("$.cpf").value(firstUser.getCpf().replaceAll("\\D", "")))
			.andExpect(jsonPath("$.firstName").value(firstUser.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(firstUser.getLastName()))
			.andExpect(jsonPath("$.birthDate").value(firstUser.getBirthDate().format(dateFormatter)))
			.andExpect(jsonPath("$.address").value(firstUser.getAddress()))
			.andExpect(jsonPath("$.phone").value(firstUser.getPhone().replaceAll("\\D", "")))
			.andDo(document("update-user", 
				pathParameters(parameterWithName("id").description("Id do usuário")),
				requestFields(getUserFields()),
				responseFields(getUserFields())
			));	
	}
	
	
	@Test
	public void test10_givenAddBulkUsersURI_whenMockMVC_thenResponseOK() throws Exception {
		
		List<User> users = new ArrayList<>();
		
		users.add(new User.UserBuilder("222.222.222-22", "Daniel", "Carvalho")
				.birthDate(LocalDate.of(1987, 10, 9)).address("Uma rua qualquer, 10").phone("(11) 99293-1111")
				.build());
		
		users.add(new User.UserBuilder("333.333.333-33", "Marcos", "Lima")
				.birthDate(LocalDate.of(1991, 1, 25)).address("Outra rua qualquer, 20").phone("(11) 99723-2222")
				.build());
		
		users.add(new User.UserBuilder("444.444.444-44", "Silvia", "Souza")
				.birthDate(LocalDate.of(1993, 7, 7)).address("Ultima rua, 30").phone("(11) 92638-3333")
				.build());
		
		FieldDescriptor[] responseFields = getUserFields();
		responseFields[0].ignored();
		
		mockMvc.perform(post("/users/bulk").content(mapper.writeValueAsString(users)).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(document("create-users-bulk", 
				requestFields(fieldWithPath("[]").description("Array de usuários"))
					.andWithPrefix("[].", responseFields))
			);
		
	}
	
	@Test
	public void test11_givenGetUsersListURI_whenMockMVC_thenResponseOK() throws Exception {

		// Checking only array length
		mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(5)));

	}
	
	@Test
	public void test12_givenDeleteUserURI_whenMockMVC_thenResponseOK() throws Exception {
		User user = new User.UserBuilder("444.444.444-44", "Silvia", "Souza").id(5L).birthDate(LocalDate.of(1993, 7, 7))
				.address("Ultima rua, 30").phone("(11) 92638-3333").build();
		
		mockMvc.perform(delete("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(document("delete-user",
				pathParameters(parameterWithName("id").description("Id do usuário"))
			));	
	}
	
	@Test
	public void test13_givenGetUserURI_whenMockMVC_thenResponseNOT_FOUND() throws Exception {
		User user = new User.UserBuilder("444.444.444-44", "Silvia", "Souza").id(5L).birthDate(LocalDate.of(1993, 7, 7))
				.address("Ultima rua, 30").phone("(11) 92638-3333").build();
		
		mockMvc.perform(get("/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON)).andDo(print())
			.andExpect(MockMvcResultMatchers.status().isNotFound());

	}
	
}
