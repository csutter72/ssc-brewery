package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by jt on 6/12/20.
 */
@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Test
    void initCreationForm() throws Exception {
        mockMvc.perform(get("/beers/new").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void findBeersNoAuth() throws Exception {
        mockMvc.perform(get("/beers/find"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void findBeersWithHttpBasicCUSTOMER() throws Exception {

        mockMvc.perform(get("/beers/find")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeersWithHttpBasicADMIN() throws Exception {

        mockMvc.perform(get("/beers/find")
                .with(httpBasic("spring", "pwd")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void findBeersWithHttpBasicUSER() throws Exception {

        mockMvc.perform(get("/beers/find")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(view().name("beers/findBeers"))
                .andExpect(model().attributeExists("beer"));
    }

    @Test
    void listBreweriesCustomerRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
            .with(httpBasic("scott", "tiger")))
            .andExpect(status().is2xxSuccessful())
            .andExpect(view().name("breweries/index"));
    }

    @Test
    void listBreweriesUserRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
            .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesAdminRole() throws Exception {
        mockMvc.perform(get("/brewery/breweries")
            .with(httpBasic("spring", "pwd")))
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesNoAuth() throws Exception {
        mockMvc.perform(get("/brewery/breweries"))
            .andExpect(status().isUnauthorized());
    }
}
