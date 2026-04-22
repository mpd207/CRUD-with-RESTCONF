package com.mock;

import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/restconf/data/ietf-interfaces:interfaces")
public class InterfaceController {

    private final NetworkInterfaceRepository repository;

    public InterfaceController(NetworkInterfaceRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Object create(@RequestBody Map<String, Object> body) {
        Map<String, Object> ifaceMap = extractInterfaceMap(body);
        NetworkInterfaceEntity entity = mapToEntity(ifaceMap);
        repository.save(entity);

        return Map.of("message", "Interface created", "data", body);
    }

    @GetMapping("/interface={name}")
    public Object get(@PathVariable String name) {
        return repository.findById(name)
                .map(this::entityToRestconfResponse)
                .orElse(Map.of("error", "Not found"));
    }

    @PutMapping("/interface={name}")
    public Object update(@PathVariable String name, @RequestBody Map<String, Object> body) {
        Map<String, Object> ifaceMap = extractInterfaceMap(body);
        NetworkInterfaceEntity entity = mapToEntity(ifaceMap);
        entity.setName(name);
        repository.save(entity);

        return Map.of("message", "Interface updated", "data", body);
    }

    @DeleteMapping("/interface={name}")
    public Object delete(@PathVariable String name) {
        if (!repository.existsById(name)) {
            return Map.of("error", "Not found");
        }

        repository.deleteById(name);
        return Map.of("message", "Interface deleted");
    }

    @GetMapping("/db/all")
    public Object showAll() {
        return repository.findAll();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractInterfaceMap(Map<String, Object> body) {
        Object raw = body.get("ietf-interfaces:interface");
        if (!(raw instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Missing 'ietf-interfaces:interface' object");
        }
        return (Map<String, Object>) map;
    }

    private NetworkInterfaceEntity mapToEntity(Map<String, Object> map) {
        return new NetworkInterfaceEntity(
                (String) map.get("name"),
                (String) map.get("description"),
                (String) map.get("type"),
                Boolean.TRUE.equals(map.get("enabled")),
                (String) map.get("ipAddress"),
                ((Number) map.get("prefixLength")).intValue()
        );
    }

    private Map<String, Object> entityToRestconfResponse(NetworkInterfaceEntity entity) {
        Map<String, Object> iface = new LinkedHashMap<>();
        iface.put("name", entity.getName());
        iface.put("description", entity.getDescription());
        iface.put("type", entity.getType());
        iface.put("enabled", entity.isEnabled());
        iface.put("ipAddress", entity.getIpAddress());
        iface.put("prefixLength", entity.getPrefixLength());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ietf-interfaces:interface", iface);
        return response;
    }
}
