package pl.straburzynski.ebooks.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.straburzynski.ebooks.model.Format;

@Slf4j
public class FileValidator {

    public static boolean isValidExtension(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(fileName);
        return (EnumUtils.isValidEnumIgnoreCase(Format.class, extension));
    }

}
