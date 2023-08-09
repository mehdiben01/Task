package Service;

import Model.Client;
import Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client save(Client client){
        return clientRepository.save(client);
    }
    public List<Client> getAllClient(){
        return clientRepository.findAllByIsDeleted("0");
    }

    public long CountClients(){
        return clientRepository.countByIsDeleted("0");
    }

    public Client getClientById(Integer id) {
        return clientRepository.getClientById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found with ID: " + id));
    }

    public Page<Object[]> getAllClients(String search, Pageable pageable){
        return clientRepository.findAllCLient(search,pageable);
    }

    public Page<Object[]> getAllClientsSupp(String search, Pageable pageable){
        return clientRepository.findAllCLientSupp(search,pageable);
    }


}
