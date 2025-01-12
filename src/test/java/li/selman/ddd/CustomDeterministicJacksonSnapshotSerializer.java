package li.selman.ddd;

import au.com.origin.snapshots.jackson.serializers.v1.DeterministicJacksonSnapshotSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jmolecules.ddd.types.AggregateRoot;

import java.time.ZonedDateTime;

public class CustomDeterministicJacksonSnapshotSerializer extends DeterministicJacksonSnapshotSerializer {

    @JsonIgnoreType
    public interface IgnoreTypeMixin {}

    interface AuditMixIn {
        @JsonIgnore
        ZonedDateTime getCreatedAt();
    }

    @Override
    public void configure(ObjectMapper objectMapper) {
        super.configure(objectMapper);
        // We want to serialize null values as well.
        // This helps devs immediately spot which fields have not been set.
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // createdAt usually is just ZonedDateTime.now('UTC')
        objectMapper.addMixIn(AggregateRoot.class, AuditMixIn.class);
    }
}
