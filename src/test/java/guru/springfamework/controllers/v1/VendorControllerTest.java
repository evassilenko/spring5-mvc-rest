package guru.springfamework.controllers.v1;

import guru.springfamework.api.v1.model.VendorDTO;
import guru.springfamework.api.v1.model.VendorListDTO;
import guru.springfamework.exceptions.ResourceNotFoundException;
import guru.springfamework.services.VendorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static guru.springfamework.controllers.v1.AbstractRestControllerTest.asJsonString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {VendorController.class})
public class VendorControllerTest extends AbstractRestControllerTest {

    public static final String NAME = "Jim";

    @MockBean //provided by Spring Context
    VendorService vendorService;

    @Autowired
    MockMvc mockMvc;
    
    VendorDTO vendorDTO_1;
    VendorDTO vendorDTO_2;

    @Before
    public void setUp() throws Exception {
    	vendorDTO_1 = new VendorDTO("Vendor 1", VendorController.BASE_URL + "/1");
        vendorDTO_2 = new VendorDTO("Vendor 2", VendorController.BASE_URL + "/2");

    }

    @Test
    public void testListVendors() throws Exception {
    	//given
    	VendorListDTO vendorListDTO = new VendorListDTO(Arrays.asList(vendorDTO_1, vendorDTO_2));
        
    	given(vendorService.getAllVendors()).willReturn(vendorListDTO.getVendors());
    	

        mockMvc.perform(get(VendorController.BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vendors", hasSize(2)));
    }

    @Test
    public void testGetVendorById() throws Exception {
    	//given
        
        given(vendorService.getVendorById(anyLong())).willReturn(vendorDTO_1);

        //when
        mockMvc.perform(get(VendorController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(vendorDTO_1.getName())));
    }
    
    @Test
    public void createNewCustomer() throws Exception {
        //given

        given(vendorService.createNewVendor(vendorDTO_1)).willReturn(vendorDTO_1);

        //when/then
        mockMvc.perform(post(VendorController.BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(vendorDTO_1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo(vendorDTO_1.getName())))
                .andExpect(jsonPath("$.vendor_url", equalTo(VendorController.BASE_URL + "/1")));
    }
    
    @Test
    public void testUpdateCustomer() throws Exception {
        //given
        given(vendorService.saveVendorByDTO(anyLong(), any(VendorDTO.class))).willReturn(vendorDTO_1);

        //when/then
        mockMvc.perform(put(VendorController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(vendorDTO_1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(vendorDTO_1.getName())))
                .andExpect(jsonPath("$.vendor_url", equalTo(VendorController.BASE_URL + "/1")));
    }
    
    @Test
    public void testPatchCustomer() throws Exception {

        //given


        given(vendorService.patchVendor(anyLong(), any(VendorDTO.class))).willReturn(vendorDTO_1);

        mockMvc.perform(patch(VendorController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(vendorDTO_1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(vendorDTO_1.getName())))
                .andExpect(jsonPath("$.vendor_url", equalTo(VendorController.BASE_URL + "/1")));
    }
    
    @Test
    public void testDeleteVendor() throws Exception {

        mockMvc.perform(delete(VendorController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(vendorService).should().deleteVendorById(anyLong());
    }
    
    @Test
    public void testNotFoundException() throws Exception {

        given(vendorService.getVendorById(anyLong()))
        	.willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get(VendorController.BASE_URL + "/222")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
