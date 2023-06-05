package antifraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class IPAddressController {

    @Autowired
    SuspiciousIPRepository suspiciousIPRepo;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/api/antifraud/suspicious-ip")
    public SuspiciousIP addSuspiciousIP(@RequestBody SuspiciousIP suspiciousIP) {

        String ipAddress = suspiciousIP.getIp();

        if (suspiciousIPRepo.existsByIp(ipAddress)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (!ValidIPAddressChecker.check(ipAddress)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        suspiciousIPRepo.save(suspiciousIP);

        return suspiciousIP;
    }

    @Transactional
    @DeleteMapping(path = "/api/antifraud/suspicious-ip/{ipAddress}")
    public Map<String, String> deleteIPAddress(@PathVariable String ipAddress) {

        if (!ValidIPAddressChecker.check(ipAddress)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!suspiciousIPRepo.existsByIp(ipAddress)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        suspiciousIPRepo.deleteByIp(ipAddress);

        Map<String, String> deleteIPAddressResponseForm = new HashMap<>();

        deleteIPAddressResponseForm.put("status", String.format("IP %s successfully removed!", ipAddress));

        return deleteIPAddressResponseForm;
    }

    @GetMapping(path = "/api/antifraud/suspicious-ip")
    public List<SuspiciousIP> getIPAddresses() {

        List<SuspiciousIP> suspiciousIPList = new ArrayList<>();

        suspiciousIPRepo.findAll().forEach(suspiciousIPList::add);

        return suspiciousIPList;
    }
}
