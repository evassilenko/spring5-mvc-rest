package guru.springfamework.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import guru.springfamework.api.v1.mapper.VendorMapper;
import guru.springfamework.api.v1.model.VendorDTO;
import guru.springfamework.controllers.v1.VendorController;
import guru.springfamework.domain.Vendor;
import guru.springfamework.exceptions.ResourceNotFoundException;
import guru.springfamework.repositories.VendorRepository;

@Service
public class VendorServiceImpl implements VendorService {
	private final VendorMapper vendorMapper;
	private final VendorRepository vendorRepository;
	
	

	public VendorServiceImpl(VendorMapper vendorMapper, VendorRepository vendorRepository) {
		super();
		this.vendorMapper = vendorMapper;
		this.vendorRepository = vendorRepository;
	}

	@Override
	public List<VendorDTO> getAllVendors() {
		
		return vendorRepository
				.findAll()
				.stream()
				.map( vendor -> {
					VendorDTO vendorDTO = vendorMapper.vendorToVendorDTO(vendor);
					vendorDTO.setVendorUrl(getVendorUrl(vendor.getId()));
					return vendorDTO;
				}).collect(Collectors.toList());
				
		
	}

	@Override
	public VendorDTO getVendorById(Long id) {
		
		return vendorRepository.findById(id)
				.map(vendorMapper::vendorToVendorDTO)
				.map(vendorDTO -> {
					vendorDTO.setVendorUrl(getVendorUrl(id));
					return vendorDTO;
				})
				.orElseThrow(ResourceNotFoundException::new);
		
	}

	@Override
	public VendorDTO createNewVendor(VendorDTO vendorDTO) {
		return saveAndReturnDTO(vendorMapper.vendorDTOToVendor(vendorDTO));
	}

	@Override
	public VendorDTO saveVendorByDTO(Long id, VendorDTO vendorDTO) {
		Vendor vendor = vendorMapper.vendorDTOToVendor(vendorDTO);
		vendor.setId(id);
		return saveAndReturnDTO(vendor);
	}
	
	/*private VendorDTO saveAndReturnDTO(Vendor vendor) {
		Vendor savedVendor = vendorRepository.save(vendor);

		VendorDTO returnDto = vendorMapper.vendorToVendorDTO(savedVendor);
		
		returnDto.setVendorUrl(getVendorUrl(vendor.getId()));

		return returnDto;
	}*/
	
	private VendorDTO saveAndReturnDTO(Vendor vendor) {

		return Optional.of(vendorMapper.vendorToVendorDTO(vendorRepository.save(vendor)))
				.map(vendorDTO -> {
					vendorDTO.setVendorUrl(getVendorUrl(vendor.getId()));
					return vendorDTO;
				})
				.orElseThrow(ResourceNotFoundException::new);
	}

	@Override
	public VendorDTO patchVendor(Long id, VendorDTO vendorDTO) {
		return vendorRepository.findById(id)
				.map(vendor -> {
					if (vendorDTO.getName() != null) {
						vendor.setName(vendorDTO.getName());
						
					}
					return saveAndReturnDTO(vendor);
				})
				.orElseThrow(ResourceNotFoundException::new);
	}

	@Override
	public void deleteVendorById(Long id) {
		vendorRepository.deleteById(id);

	}

	private String getVendorUrl(Long id) {
        return VendorController.BASE_URL + "/" + id;
    }
}
