package com.kalsym.product.service.controller;

//Importing Models
//Importing Enums
import com.kalsym.product.service.enums.VoucherStatus;
import com.kalsym.product.service.enums.VoucherType;
//Importing Repositories
import com.kalsym.product.service.repository.*;
//Importing Utilities
import com.kalsym.product.service.utility.HttpResponse;
import com.kalsym.product.service.utility.Logger;

//Importing Java Utils
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

//Importing Swagger
import io.swagger.annotations.ApiOperation;

//Importing Spring framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * @author ayaan
 */
@RestController
@RequestMapping("/voucher")
public class StoreProjectVoucherController {


}
