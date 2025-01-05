package com.example.Proje3.Controllers;

import com.example.Proje3.Repositorys.LogsRepository;
import com.example.Proje3.Tablolar.Customers;
import com.example.Proje3.Tablolar.Logs;
import org.springframework.stereotype.Controller;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LogController {
    private static final List<Logs> logs = new ArrayList<>();
    private int logIdCounter = 1;
    private LogsRepository logsRepository;

    public LogController(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    public synchronized void addLog(int customerId, String productName, int quantity, String logType, String result, Customers.CustomerType customerType) {
        Logs log = new Logs();
        log.setLogId(logIdCounter++);
        log.setCustomerID(customerId);
        log.setProductName(productName);
        log.setMiktar(String.valueOf(quantity));
        log.setLogType(Logs.LogType.valueOf(logType));
        log.setLogDetails(result);
        log.setLogDate(new Date(System.currentTimeMillis()));
        log.setCustomerType(customerType);
        logsRepository.save(log);
        logs.add(log);
    }


}
