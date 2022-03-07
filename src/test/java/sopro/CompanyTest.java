package sopro;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.xmlunit.builder.Input;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import sopro.security.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyTest {
    
    @Autowired
    private MockMvc mockMvc;


    /**
     * Tests, if the Company screen is served on /myCompany.
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "admin@admin", roles = {"ADMIN"})
    public void adminAddCompany() throws Exception {
        mockMvc.perform(get("/companies"))
               .andExpect(status().is(200));
               
        mockMvc.perform(get("/companies/add"))
               .andExpect(status().is(200));              

        mockMvc.perform(post("/companies/save").param("name", "187Comp.").with(csrf()))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/companies"))
               .andExpect(status().isFound());       

    }
    

    


}
