# dynamic-field-filtering
SpringBoot microservice which supports dynamic field filtering from the response

# Steps to follow :

1. Add the dependencies 
```aidl
<dependency>
	<groupId>com.github.bohnman</groupId>
	<artifactId>squiggly-filter-jackson</artifactId>
	<version>1.3.18</version>
</dependency>
```

2. Add this code

```aidl
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

@ControllerAdvice
public class DynamicFieldFilteringConfiguration implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicFieldFilteringConfiguration.class);

    private static final String REQUIRED_FIELDS_PARAM_KEY = "fields";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Inorder to invoke beforeBodyWrite we should return true
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        final String requiredFieldString =
                ((ServletServerHttpRequest) request).getServletRequest().getParameter(REQUIRED_FIELDS_PARAM_KEY);
        if (null != requiredFieldString) {
            final ObjectMapper objectMapper = Squiggly.init(new ObjectMapper(), requiredFieldString);
            final String stringifyResponse = SquigglyUtils.stringify(objectMapper, body);
            try {
                return objectMapper.readValue(stringifyResponse,Object.class);
            } catch (IOException e) {
                logger.error("Failed to Parse the response : " + e.getMessage(),e);
            }
        }
        return body;
    }
}
```

3. Now lets see how does it work with both XML and JSON response.
import the DynamicFieldFiltering.postman_collection.json into postman collection and test.

# Test 1

Request
```aidl
http://localhost:8080/employees
```
Response
```aidl
[
    {
        "firstName": "Suraj",
        "middleName": "Panduranga",
        "lastName": "Jannu",
        "age": 26,
        "salary": 100000
    },
    {
        "firstName": "Pramod",
        "lastName": "Jannu",
        "age": 29,
        "salary": 300000
    },
    {
        "firstName": "Mohan",
        "lastName": "Mari",
        "age": 49,
        "salary": 200000
    },
    {
        "firstName": "Abhilash",
        "age": 34,
        "salary": 400000
    },
    {
        "firstName": "Harsha",
        "age": 28,
        "salary": 500000
    },
    {
        "firstName": "Imran",
        "age": 26,
        "salary": 800000
    }
]
```


# Test 2

Request
```aidl
http://localhost:8080/employees.xml?fields=firstName,middleName,lastName
```
Response
```aidl
<List>
    <item>
        <firstName>Suraj</firstName>
        <middleName>Panduranga</middleName>
        <lastName>Jannu</lastName>
    </item>
    <item>
        <firstName>Pramod</firstName>
        <lastName>Jannu</lastName>
    </item>
    <item>
        <firstName>Mohan</firstName>
        <lastName>Mari</lastName>
    </item>
    <item>
        <firstName>Abhilash</firstName>
    </item>
    <item>
        <firstName>Harsha</firstName>
    </item>
    <item>
        <firstName>Imran</firstName>
    </item>
</List>
```

# Test 3

Request
```aidl
http://localhost:8080/employees.json?fields=firstName,middleName,lastName
```
Response
```aidl
[
    {
        "firstName": "Suraj",
        "middleName": "Panduranga",
        "lastName": "Jannu"
    },
    {
        "firstName": "Pramod",
        "lastName": "Jannu"
    },
    {
        "firstName": "Mohan",
        "lastName": "Mari"
    },
    {
        "firstName": "Abhilash"
    },
    {
        "firstName": "Harsha"
    },
    {
        "firstName": "Imran"
    }
]
```

# Note :
```aidl
Check the Licence of squiggly before using
```