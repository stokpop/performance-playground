package nl.stokpop.server;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class StokpopController {

    private final MakePdfService pdfService;

    @RequestMapping(path = "/download-bytes", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFileBytes() throws IOException {

        byte[] bytes = pdfService.makePdfHelloWorldByteArray();

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=helloworld.pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(bytes.length)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @RequestMapping(path = "/download-stream", method = RequestMethod.GET)
    public StreamingResponseBody downloadFileStream() {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=helloworld.pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return pdfService::makePdfHelloWorldOutputStream;
    }

}
