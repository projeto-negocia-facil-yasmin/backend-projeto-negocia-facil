package br.edu.ifpb.dac.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import br.edu.ifpb.dac.dto.AdvertisementOwnerDTO;
import br.edu.ifpb.dac.dto.AdvertisementRequestDTO;
import br.edu.ifpb.dac.dto.AdvertisementResponseDTO;
import br.edu.ifpb.dac.dto.ProductDTO;
import br.edu.ifpb.dac.dto.UserResponseDTO;
import br.edu.ifpb.dac.entity.Advertisement;
import br.edu.ifpb.dac.entity.Product;
import br.edu.ifpb.dac.entity.User;
import br.edu.ifpb.dac.mapper.AdvertisementMapper;
import br.edu.ifpb.dac.mapper.ProductMapper;
import br.edu.ifpb.dac.mapper.UserMapper;
import br.edu.ifpb.dac.repository.AdvertisementRepository;
import br.edu.ifpb.dac.repository.ProductRepository;
import br.edu.ifpb.dac.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ProductMapper productMapper;
    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    

    public void saveAdvertisement(AdvertisementRequestDTO advertisementDTO) {
        Advertisement advertisement = AdvertisementMapper.toEntity(advertisementDTO);
        User user = userRepository.findById(advertisementDTO.advertiser().id())
           .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        advertisement.setAdvertiser(user);
        advertisementRepository.save(advertisement);
    }

    public List<AdvertisementResponseDTO> getAllAdvertisements() {
        return advertisementRepository.findAll().stream()
            .map(AdvertisementMapper::toResponseDTO)
            .toList();
    }

    public AdvertisementResponseDTO getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));
        return AdvertisementMapper.toResponseDTO(advertisement);
    }

    public void deleteAdvertisement(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));
        advertisementRepository.delete(advertisement);
    }

    public void updateAdvertisement(Long id, AdvertisementResponseDTO advertisementDTO) {
        Advertisement advertisement = advertisementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

        User advertiser = userRepository.findById(advertisementDTO.advertiser().id())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Product> products = new ArrayList<>();
            for (ProductDTO productDTO : advertisementDTO.products()) {
                Product product = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
                products.add(product);
            }
        
        advertisement.setAdvertiser(advertiser);
        advertisement.setDescription(advertisementDTO.description());
        advertisement.setCreatedAt(advertisementDTO.createdAt());
        advertisement.setProducts(products);

        advertisementRepository.save(advertisement);
    }

}
