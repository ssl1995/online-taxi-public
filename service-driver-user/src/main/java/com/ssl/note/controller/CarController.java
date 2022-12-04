package com.ssl.note.controller;


import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/car")
    public ResponseResult<String> addCar(@RequestBody Car car) {
        return carService.addCar(car);
    }

    @GetMapping("/car")
    public ResponseResult<Car> getCarById(@RequestParam("carId") Long carId) {
        return carService.getCarById(carId);
    }
}
