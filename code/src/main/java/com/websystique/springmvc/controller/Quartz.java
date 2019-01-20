package com.websystique.springmvc.controller;

import com.websystique.springmvc.model.Browser;
import com.websystique.springmvc.model.UserDocument;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.quartz.JobBuilder.newJob;

/**
 * Created by admin on 25/2/2016.
 */

@Component
public class Quartz {

    private static String LOC;
    private static String SLOC;

    @Value("${path.location}")
    private void setLoc(String privateLoc) {
        Quartz.LOC = privateLoc;
    }

    @Value("${selenium.location}")
    private void setSLoc(String privateSLoc) {
        Quartz.SLOC = privateSLoc;
    }


    public static void scheduleJob(UserDocument doc, Browser browser) {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler scheduler = sf.getScheduler();

            JobKey jobKey = new JobKey(doc.getId().toString(), doc.getId().toString());
            if (doc.getCron().equals("")) {
                scheduler.deleteJob(jobKey);
            } else {
                scheduler.deleteJob(jobKey); //delete old trigger, if any, and the corresponding user information

                JobDetail job = newJob(QuartzJob.class)
                        .withIdentity(doc.getId().toString(), doc.getId().toString())
                        .build();

                Trigger trigger = TriggerBuilder
                        .newTrigger()
                        .withIdentity(doc.getId().toString(), doc.getId().toString())
                        .withSchedule(
                                CronScheduleBuilder.cronSchedule(doc.getCron()))
                        .build();

                job.getJobDataMap().put("docObject", doc);
                job.getJobDataMap().put("browserObject", browser);

                //Listener attached to jobKey
                scheduler.getListenerManager().addJobListener(
                        new QuartzListener(), KeyMatcher.keyEquals(jobKey)
                );


                scheduler.start();
                scheduler.scheduleJob(job, trigger);

            }


        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }


    public static void runMethod(UserDocument document, Browser browser) throws IOException, ParseException {

        //declaration and instantiation of objects/variables
        System.out.println("Test case is running.");
        System.setProperty("webdriver.chrome.driver",browser.getWebdriver());
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        String baseUrl = "https://images.google.com/";
        String expectedText = "No other sizes of this image found.";

        // launch Chrome and direct it to the Base URL
        driver.get(baseUrl);

        // get the actual value
        driver.findElement(By.id("qbi")).click();
        driver.findElement(By.linkText("Upload an image")).click();
        driver.findElement(By.id("qbfile")).click();
        driver.findElement(By.id("qbfile")).clear();
        driver.findElement(By.id("qbfile")).sendKeys(SLOC+document.getId()+"\\"+document.getName());
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        //FileUtils.cleanDirectory(f);

        if (expectedText.equals(driver.findElement(By.cssSelector("div.O1id0e")).getText())){
            System.out.println("Test Passed!");
            File f = new File(LOC +document.getId()+"/Pass_Google_Search.png");
            FileUtils.copyFile(scrFile, f);
        } else {
            System.out.println("Test Failed");
            File f = new File(LOC +document.getId()+"/Fail_Google_Search.png");
            FileUtils.copyFile(scrFile, f);
        }

        //close Chrome
        driver.close();

//        String tempName = FilenameUtils.removeExtension(document.getName());
//
//        File dir = new File(LOC + document.getId() + "/" + tempName + "/");
//        boolean checkFail = true;
//        for (File file : dir.listFiles()) {
//            if (file.getName().endsWith((".png"))) {
//                checkFail = false;
//            }
//
//        }
//        if (dir.listFiles().length < 2) {
//            checkFail = false;
//        }
//        System.out.println(checkFail);
//        boolean isEmpty = true;
//        if (dir.listFiles().length == 0) {
//            isEmpty = false; //empty means something is wrong with code. Look at the bloody code
//        }
//
//        System.out.println(isEmpty);
//        try {
//            Email.generateAndSendEmail(document, timestamp, checkFail, isEmpty);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }


    }
}
