package guru.springfamework.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import guru.springfamework.api.v1.mapper.VendorMapper;
import guru.springfamework.api.v1.model.VendorDTO;
import guru.springfamework.controllers.v1.VendorController;
import guru.springfamework.domain.Vendor;
import guru.springfamework.repositories.VendorRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.hamcrest.core.IsEqual.equalTo;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VendorServiceImplTest {

    public static final String NAME_1 = "My Vendor";
    public static final long ID_1 = 1L;
    public static final String NAME_2 = "My Vendor";
    public static final long ID_2 = 1L;

    VendorService vendorService;

    @Mock
    VendorRepository vendorRepository;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        vendorService = new VendorServiceImpl(VendorMapper.INSTANCE, vendorRepository);
    }

    @Test
    public void getAllVendors() throws Exception {

        //given
        List<Vendor> vendors = Arrays.asList(getVendor1(),getVendor2());

        given(vendorRepository.findAll()).willReturn(vendors);

        //when
        List<VendorDTO> vendorDTOS = vendorService.getAllVendors();

        //then
        then(vendorRepository).should(times(1)).findAll();
        assertThat(vendorDTOS.size(), is(equalTo(2)));

    }

    @Test
    public void getVendorByID() throws Exception {

        //given
    	
    	Vendor vendor = getVendor1();
    	

        given(vendorRepository.findById(anyLong())).willReturn(Optional.ofNullable(vendor));

        //when
        VendorDTO vendorDTO = vendorService.getVendorById(1L);

        //JUnit Assert that with matchers
        assertThat(vendorDTO.getName(), is(equalTo(NAME_1)));

    }
    
    @Test
    public void createNewVendor() throws Exception {

        //given
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setName(NAME_1);

        Vendor savedVendor = getVendor1();

        given(vendorRepository.save(any(Vendor.class))).willReturn(savedVendor);

        //when
        VendorDTO savedVendorDTO = vendorService.createNewVendor(vendorDTO);

        //then
        // 'should' defaults to times = 1
        then(vendorRepository).should().save(any(Vendor.class));
        assertThat(savedVendorDTO.getVendorUrl(), containsString("1"));
        
    }
    
    @Test
    public void saveVendorByDTO() throws Exception {

        //given
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setName("Jim");

        Vendor savedVendor = new Vendor();
        savedVendor.setName(vendorDTO.getName());
        savedVendor.setId(1l);

        given(vendorRepository.save(any(Vendor.class))).willReturn(savedVendor);

        //when
        VendorDTO savedVendorDTO = vendorService.saveVendorByDTO(1L, vendorDTO);

        //then
        then(vendorRepository).should().save(any(Vendor.class));
        assertThat(savedVendorDTO.getVendorUrl(), containsString("1"));
    }
    
    @Test
    public void deleteVendorById() throws Exception {

        Long id = 1L;

        vendorRepository.deleteById(id);

        verify(vendorRepository, times(1)).deleteById(anyLong());
    }
    
    private Vendor getVendor1() {
        Vendor vendor = new Vendor();
        vendor.setName(NAME_1);
        vendor.setId(ID_1);
        return vendor;
    }

    private Vendor getVendor2() {
        Vendor vendor = new Vendor();
        vendor.setName(NAME_2);
        vendor.setId(ID_2);
        return vendor;
    }
}
