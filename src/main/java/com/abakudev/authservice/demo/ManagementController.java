package com.abakudev.authservice.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
public class ManagementController {

    @Operation(
            description = "Get endpoint for manager.",
            summary = "This is a summary for management get endpoint.")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized / Invalid Token",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Forbiden",
                    responseCode = "403"
            )
    })
    @GetMapping
    public String get() {
        return "GET:: management controller";
    }

    @Operation(
            description = "Post endpoint for manager.",
            summary = "This is a summary for management post endpoint.")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized / Invalid Token",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Forbiden",
                    responseCode = "403"
            )
    })
    @PostMapping
    public String post() {
        return "POST:: management controller";
    }

    @Operation(
            description = "Put endpoint for manager.",
            summary = "This is a summary for management put endpoint.")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized / Invalid Token",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Forbiden",
                    responseCode = "403"
            )
    })
    @PutMapping
    public String put() {
        return "PUT:: management controller";
    }

    @Operation(
            description = "Delete endpoint for manager.",
            summary = "This is a summary for management delete endpoint.")
    @ApiResponses(value = {
            @ApiResponse(
                    description = "Success",
                    responseCode = "200"
            ),
            @ApiResponse(
                    description = "Unauthorized / Invalid Token",
                    responseCode = "401"
            ),
            @ApiResponse(
                    description = "Forbiden",
                    responseCode = "403"
            )
    })
    @DeleteMapping
    public String delete() {
        return "DELETE:: management controller";
    }
}
