package edu.uci.ics.textdb.web.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.textdb.web.response.TextdbWebResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Path("/upload")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class FileUploadResource {
    @POST
    @Path("/dictionary")
    public Response uploadDictionaryFile(@FormDataParam("file") InputStream uploadedInputStream,
                                         @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {

        StringBuilder dictionary = new StringBuilder();

        String line = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(uploadedInputStream))) {
            while ((line = br.readLine()) != null) {
                dictionary.append(line);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();

        TextdbWebResponse textdbWebResponse = new TextdbWebResponse(0, dictionary.toString());
        return Response.status(200)
                .entity(objectMapper.writeValueAsString(textdbWebResponse))
                .build();
    }
}