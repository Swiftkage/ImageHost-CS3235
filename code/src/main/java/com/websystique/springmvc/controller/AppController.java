package com.websystique.springmvc.controller;

import com.websystique.springmvc.model.*;
import com.websystique.springmvc.service.BrowserService;
import com.websystique.springmvc.service.UserDocumentService;
import com.websystique.springmvc.service.UserService;
import com.websystique.springmvc.util.File2Validator;
import com.websystique.springmvc.util.FileValidator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zeroturnaround.zip.ZipUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping("/")
public class AppController {

    @Value("${path.location}")
    private String LOC;

    @Value("${number.id}")
    private int USERID; //yes, the user id is static for now, but the database is alr created. Login as future enhancement

    @Autowired
    UserService userService;

    @Autowired
    UserDocumentService userDocumentService;

    @Autowired
    BrowserService browserService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    FileValidator fileValidator;

    @Autowired
    File2Validator file2Validator;

    @InitBinder("fileBucket")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(fileValidator);
    }

    @InitBinder("fileChange")
    protected void initBinder2(WebDataBinder binder) {
        binder.setValidator(file2Validator);
    }

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String Home(ModelMap model) {

        return "home";
    }

    @RequestMapping(value = {"/404"}, method = RequestMethod.GET)
    public String Redirect404(ModelMap model) {

        return "404";
    }

    @RequestMapping(value = {"/500"}, method = RequestMethod.GET)
    public String Redirect500(ModelMap model) {

        return "500";
    }

    @RequestMapping(value = {"/generic"}, method = RequestMethod.GET)
    public String Redirect5Generic(ModelMap model) {

        return "errorgeneric";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String Index(ModelMap model) {
        List<UserDocument> documents = userDocumentService.findAllByUserId(USERID);
        List<FileChange> listOfFiles = new LinkedList<FileChange>();

        for (int i = 0; i < documents.size(); i++) {
            FileChange f = new FileChange();
            f.setId(documents.get(i).getId());
            f.setName(documents.get(i).getName());
            f.setDescription(documents.get(i).getDescription());
            f.setType(documents.get(i).getType());
            f.setContent(Base64.getEncoder().encodeToString(documents.get(i).getContent()));
            f.setCron(documents.get(i).getCron());
            f.setInfo(documents.get(i).getInfo());
            listOfFiles.add(f);
        }
        model.addAttribute("fileChange", listOfFiles);

        return "index";
    }

    @RequestMapping(value = {"/image/upload"}, method = RequestMethod.GET)
    public String scriptUpload(ModelMap model) {
        FileBucket fileModel = new FileBucket();
        model.addAttribute("fileBucket", fileModel);
        return "uploadscript";
    }


    @RequestMapping(value = {"/image/upload"}, method = RequestMethod.POST)
    public String uploadDoc(@Valid FileBucket fileBucket, BindingResult result, ModelMap model
            , RedirectAttributes redirectAttrs) throws IOException, ParseException {

        if (result.hasErrors()) {
            System.out.println("validation errors");

            return "uploadscript";
        } else {
            System.out.println("Fetching file");
            User user = userService.findById(USERID);
            Browser browser = browserService.findByName(user.getBrowser());
            String name = saveDocument(fileBucket, user, browser);

            redirectAttrs.addFlashAttribute("MESSAGE5",
                    name + " has been successfully added!");
            return "redirect:/index";
        }
    }


    @RequestMapping(value = {"/image/{docId}"}, method = RequestMethod.GET)
    public String Documents(@PathVariable int docId, ModelMap model) throws IOException {

        UserDocument document = userDocumentService.findById(docId);

        FileChange f = new FileChange();
        f.setId(document.getId());
        f.setName(FilenameUtils.removeExtension(document.getName()));
        f.setExtension(FilenameUtils.getExtension(document.getName()));
        f.setDescription(document.getDescription());
        f.setContent(new String(document.getContent()));
        f.setCron(document.getCron());
        model.addAttribute("fileChange", f);
        model.addAttribute("document", document);

        return "managedoc";
    }

//    @RequestMapping(value = {"/image/{docId}"}, method = RequestMethod.POST)
//    public String updateDoc(@Valid FileChange fileChange, BindingResult result, ModelMap model, @PathVariable int docId,
//                            RedirectAttributes redirectAttrs) throws IOException {
//
//        if (result.hasErrors()) {
//            System.out.println("validation errors");
//
//            UserDocument document = userDocumentService.findById(docId);
//
//            model.addAttribute("expandcollapse", "false");
//            model.addAttribute("document", document);
//
//            return "managedoc";
//        } else {
//
//            UserDocument document = userDocumentService.findById(docId);
//
//            String name = updateDocument(document, fileChange);
//            User user = userService.findById(USERID);
//            Browser browser = browserService.findByName(user.getBrowser());
//
//            Quartz.scheduleJob(document, browser);
//            redirectAttrs.addFlashAttribute("MESSAGE2",
//                    name + " has been successfully updated!");
//            return "redirect:/image/" + docId;
//        }
//    }

//    @RequestMapping(value = {"/{docId}/image"}, method = RequestMethod.GET)
//    public void dlImage(@PathVariable int docId, ModelMap model, HttpServletResponse response) throws IOException {
//        UserDocument document = userDocumentService.findById(docId);
//        response.setContentType(document.getType());
//        response.setContentLength(document.getContent().length);
//        response.setHeader("Content-Disposition", "attachment; filename=\"" + document.getName() + "\"");
//
//        FileCopyUtils.copy(document.getContent(), response.getOutputStream());
//    }

    @RequestMapping(value = {"/{docId}/tsa"}, method = RequestMethod.GET)
    public String dlTSA(@PathVariable int docId, ModelMap model, RedirectAttributes redirectAttrs,
                        HttpServletResponse response) throws IOException {

        UserDocument document = userDocumentService.findById(docId);
        String tempName = FilenameUtils.removeExtension(document.getName());
        File f = new File(LOC + docId + ".zip");
        if (f.exists()) {
            response.setContentType("application/zip");
            response.setContentLength((int) f.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + tempName + ".zip\"");
            Path path = Paths.get(LOC + docId + ".zip");
            byte[] zipFileBytes = Files.readAllBytes(path);

            FileCopyUtils.copy(zipFileBytes, response.getOutputStream());
        }
        else{
            File f2 = new File(LOC + docId);
            if (f2.list().length == 2) {
                redirectAttrs.addFlashAttribute("MESSAGE3",
                        "Image is not original!");
            }
            else{
                redirectAttrs.addFlashAttribute("MESSAGE",
                        "Run the verification first!");
            }
        }
        return "redirect:/image/" + docId;
    }

    @RequestMapping(value = {"/{docId}/key"}, method = RequestMethod.GET)
    public String dlKey(@PathVariable int docId, ModelMap model, RedirectAttributes redirectAttrs,
                        HttpServletResponse response) throws IOException {
        UserDocument document = userDocumentService.findById(docId);

        File f = new File(LOC + docId + "_Dis.zip");

        if (f.exists()) {
            String tempName = "Dis_" + FilenameUtils.removeExtension(document.getName());
            response.setContentType("application/zip");
            response.setContentLength((int) f.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + tempName + ".zip\"");
            Path path = Paths.get(LOC + docId + "_Dis.zip");
            byte[] zipFileBytes = Files.readAllBytes(path);

            FileCopyUtils.copy(zipFileBytes, response.getOutputStream());
        }
        else{
            File f2 = new File(LOC + docId);
            if (f2.list().length == 2) {
                redirectAttrs.addFlashAttribute("MESSAGE3",
                        "Image is not original!");
            }
            else{
                redirectAttrs.addFlashAttribute("MESSAGE",
                        "Run the verification first!");
            }
        }
        return "redirect:/image/" + docId;
    }


    @ResponseBody
    @RequestMapping(value = {"/rundoc"}, method = RequestMethod.POST)
    public void runDoc(int docId) throws Exception {
        UserDocument document = userDocumentService.findById(docId);
        User user = userService.findById(USERID);
        Browser browser = browserService.findByName(user.getBrowser());

        Quartz.runMethod(document, browser);
        File f = new File(LOC + docId + "/Pass_Google_Search.png");
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy.HH.mm.ss").format(new Date());
        if (!f.exists()) {
            document.setInfo("On " + timeStamp + " Google Reverse image search returned that the image is NOT original");
        } else {
            combineImages(document);
            TSA.runTSA(document);
            document.setInfo("On " + timeStamp + " Google Reverse search returned that the image is original");
        }
        userDocumentService.updateDocument(document);
        File f2 = new File(LOC + docId);
        if (f2.list().length == 5) {
            saveAsZip(document);
        }

        if (f2.list().length == 5) {
            createDirectory2(document);
            Key key = generateKey();
            saveKeyToFile(document, key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            saveImageToFile(document, cipher);
            saveAsZip2(document);
        }

    }

    @ResponseBody
    @RequestMapping(value = {"/deletedoc"}, method = RequestMethod.POST)
    public String deleteDoc(int docId, RedirectAttributes redirectAttrs) throws IOException {
        final UserDocument document = userDocumentService.findById(docId);
        String scriptName = document.getName();

        //delete document directory
        File f = new File(LOC + docId + "/");
        if (f.exists()) {
            FileUtils.deleteDirectory(f);
        }

        //delete distribution directory
        File f2 = new File(LOC + docId + "_Dis/");
        if (f2.exists()) {
            FileUtils.deleteDirectory(f2);
        }

        //delete zip file
        File f3 = new File(LOC + docId + ".zip");
        if (f3.exists()) {
            f3.delete();
        }

        //delete distribution zip
        File f4 = new File(LOC + docId + "_Dis.zip");
        if (f4.exists()) {
            f4.delete();
        }

        //delete schedule
        document.setCron("");
        Quartz.scheduleJob(document, null);

        userDocumentService.deleteById(docId);

        redirectAttrs.addFlashAttribute("MESSAGE6",
                "image has been successfully deleted!");
        return "redirect:/index";
    }


    @RequestMapping(value = {"/user"}, method = RequestMethod.GET)
    public String UserInfo(ModelMap model) {

        User user = userService.findById(USERID);
        List<Browser> browsers = browserService.findAllBrowsers();
        model.addAttribute("user", user);
        model.addAttribute("browsers", browsers);
        return "user";
    }

    @RequestMapping(value = {"/user"}, method = RequestMethod.POST)
    public String UserInfoPOST(@Valid User userUpdate, BindingResult result, ModelMap model,
                               RedirectAttributes redirectAttrs) throws IOException {

        if (result.hasErrors()) {
            System.out.println("validation errors");
            return "user";
        } else {
            boolean check = false;
            User user = userService.findById(USERID);
            if (!user.getEmail().equals(userUpdate.getEmail())) { //update scheduling jobs,if email was changed
                check = true;
            } else if (!user.getBrowser().equals(userUpdate.getBrowser())) { //update scheduling jobs,if email was changed
                check = true;
            }
            userService.updateUser(userUpdate);

            if (check) {
                List<UserDocument> documents = userDocumentService.findAllByUserId(USERID);
                Browser browser = browserService.findByName(userUpdate.getBrowser());
                for (UserDocument doc : documents
                ) {
                    Quartz.scheduleJob(doc, browser);
                }
            }

            redirectAttrs.addFlashAttribute("MESSAGE",
                    "User has been successfully updated!");
            return "redirect:/user";
        }
    }

    @RequestMapping(value = {"/about"}, method = RequestMethod.GET)
    public String About(ModelMap model) {

        return "about";
    }


    private String saveDocument(FileBucket fileBucket, User user, Browser browser) throws IOException, ParseException {

        UserDocument document = new UserDocument();

        MultipartFile multipartFile = fileBucket.getFile();

        document.setName(multipartFile.getOriginalFilename());
        document.setDescription(fileBucket.getDescription());
        document.setType(multipartFile.getContentType());
        document.setContent(multipartFile.getBytes());
        document.setUser(user);
        document.setCron("");
        document.setInfo("Not verified yet");
        userDocumentService.saveDocument(document);

        createDirectory(document);

        return multipartFile.getOriginalFilename();
    }

    private String updateDocument(UserDocument document, FileChange file) throws IOException {
        String tmpName = file.getName() + "." + file.getExtension();

        //delete file if the name is changed
        if (!document.getName().equals(tmpName)) {
            File f = new File(LOC + document.getId() + "/" + document.getName());
            if (f.exists()) {
                f.delete();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        document.setName(tmpName);
        document.setDescription(file.getDescription());
        document.setCron(file.getCron());
        System.out.println(document.toString());
        userDocumentService.updateDocument(document);

        File f2 = new File(LOC + document.getId() + "/" + tmpName);
        f2.createNewFile();
        FileCopyUtils.copy(document.getContent(), f2);

        saveAsZip(document);

        return tmpName;
    }

    private void createDirectory(UserDocument document) throws IOException {
        File dir = new File(LOC + document.getId());
        dir.mkdir();
        File file = new File(LOC + document.getId() + "/" + document.getName());
        file.createNewFile();
        FileCopyUtils.copy(document.getContent(), file);
    }

    private void createDirectory2(UserDocument document) throws IOException {
        File dir = new File(LOC + document.getId() + "_Dis");
        dir.mkdir();
    }

    private void saveAsZip(UserDocument document) {
        File f = new File(LOC + document.getId());
        File newf = new File(LOC + document.getId() + ".zip");
        ZipUtil.pack(f, newf);
    }

    private void saveAsZip2(UserDocument document) {
        File f = new File(LOC + document.getId() + "_Dis");
        File newf = new File(LOC + document.getId() + "_Dis.zip");
        ZipUtil.pack(f, newf);
    }

    private void combineImages(UserDocument document) throws IOException {
        File f = new File(LOC + document.getId() + "/" + document.getName());
        File f2 = new File(LOC + document.getId() + "/Pass_Google_Search.png");
        if (!f2.exists())
            f2 = new File(LOC + document.getId() + "/Fail_Google_Search.png");

        BufferedImage a = ImageIO.read(f);
        BufferedImage b = ImageIO.read(f2);


        int offset = 2;
        int width = a.getWidth() + b.getWidth() + offset;
        int height = Math.max(a.getHeight(), b.getHeight()) + offset;
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.BLACK);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(a, null, 0, 0);
        g2.drawImage(b, null, a.getWidth() + offset, 0);
        g2.dispose();
        ImageIO.write(newImage, "png", new File(LOC + document.getId() + "/combine.png"));

    }

    // Generate AES Key
    public Key generateKey() throws NoSuchAlgorithmException, NoSuchPaddingException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        Key key = keyGen.generateKey();
        return key;
    }

    // Saves the Key in base64 to a File
    public void saveKeyToFile(UserDocument document, Key key) throws IOException {
        byte[] keyInBytes = key.getEncoded();
        byte[] keyInBase64 = Base64.getEncoder().encode(keyInBytes);
        String tempName = FilenameUtils.removeExtension(document.getName());

        File file = new File(LOC + document.getId() + "_Dis/Key_" + tempName + ".txt");
        file.createNewFile();

        FileUtils.writeByteArrayToFile(file, keyInBase64);
    }

    // Saves the encrypted image to a File
    public void saveImageToFile(UserDocument document, Cipher cipher) throws
            IOException, BadPaddingException, IllegalBlockSizeException {
        byte[] imageFile = document.getContent();
        byte[] encImage = cipher.doFinal(imageFile);
        byte[] base64Value = Base64.getEncoder().encode(encImage);

        File file = new File(LOC + document.getId() + "_Dis/Encrypted_" + document.getName());
        file.createNewFile();

        FileUtils.writeByteArrayToFile(file, base64Value);
    }
}
