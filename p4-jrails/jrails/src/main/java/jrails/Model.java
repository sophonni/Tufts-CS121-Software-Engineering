package jrails;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class Model {
    private static Map<Integer, Map<String, Object>> AllModels = new LinkedHashMap<Integer, Map<String, Object>>();
    public static int modelID = 0;
    public int currModelID = 0;
    private static int lastIDFromDB = 0;
    private static String dbFileName = "Book_DB.txt";
    public Model() {
        currModelID = 0;
    }

    public static void TestHelper()
    {
        System.out.println("All Books: " + AllModels);
    }

    public void save() {
        Model.readAndStoreData();

        /* get the class of the newly created model */
        Class<?> runtimeClassOfGivenClass = this.getClass();

        /* get info of the newly created model and store them */
        Field[] publicFields = runtimeClassOfGivenClass.getFields();
        Map<String, Object> infoOfAModel = getModelInfoAndStore(publicFields, runtimeClassOfGivenClass);
        System.out.println("ID is: " + this.id());
        if (this.currModelID == 0)
        {
            modelID = this.lastIDFromDB + 1;
            this.currModelID = modelID;
            AllModels.put(this.currModelID, infoOfAModel);
        }
        else
        {
            System.out.println("Already Exist");
            /* ensure that the ID of a model exist before modifying the model's info */
            if (!AllModels.containsKey(this.currModelID))
            {
                throw new IllegalArgumentException("Error in {getModelInfoAndStore} function: model with ID {" + this.modelID + "} does not exist in the DB.");
            }
            else
            {
                AllModels.put(this.currModelID, infoOfAModel);
            }
        }
        storeBookInDB(this.currModelID);
        TestHelper();
    }

    private static void readAndStoreData()
    {
        try
        {
            File file = new File(dbFileName);
            if(file.exists() && (file.length() != 0))
            {
                BufferedReader reader = new BufferedReader(new FileReader(dbFileName));
                String currLine = reader.readLine();

                Map<String, Object> fieldNameAndToValueKVP = new LinkedHashMap<String, Object>();
                List<Object> currLineInfo;
                while (currLine != null)
                {
                    currLineInfo = new ArrayList<>();
                    String currInfoOfCurrLine = "";
                    for (int i = 0; i < currLine.length(); i++)
                    {
                        char currChar = currLine.charAt(i);
                        if (currChar != ',' )
                        {
                            currInfoOfCurrLine += currChar;
                        }
                        else
                        {
                            currInfoOfCurrLine = currInfoOfCurrLine.replaceFirst("^\\s+", "");
                            currLineInfo.add(currInfoOfCurrLine);
                            currInfoOfCurrLine = "";
                        }
                    }
                    /* add the last segment to currLineInfo after the loop ends */
                    if (!currInfoOfCurrLine.isEmpty()) {
                        currLineInfo.add(currInfoOfCurrLine);
                    }
                    
                    int id = 0;
                    Map<String, Object> currLineModel = new LinkedHashMap<String, Object>();
                    for (int j = 0; j < currLineInfo.size(); j++)
                    {
                        String fullInfo = (String)currLineInfo.get(j);
                        String fieldName = fullInfo.substring(0, fullInfo.indexOf(":"));
                        Object valueOfField = fullInfo.substring(fullInfo.indexOf(":") + 2, fullInfo.length());

                        if (fieldName.contains("ID"))
                        {
                            id = Integer.valueOf((String) valueOfField);
                            lastIDFromDB = id;

                        }
                        else if (fieldName.contains("Model_Type"))
                        {
                            currLineModel.put("Model_Type", valueOfField);
                        }
                        else
                        {
                            if (valueOfField.toString().matches("[0-9]+"))
                            {
                                currLineModel.put(fieldName, Integer.valueOf((String) valueOfField));
                            }
                            else
                            {
                                currLineModel.put(fieldName, valueOfField);
                            }
                        }
                    }
                    AllModels.put(id, currLineModel);
                    
                    currLine = reader.readLine();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void storeBookInDB(int currModelID)
    {
        try
        {
            String modelInfo = null;
            /* write all added models and their info, to the DB (i.e output file) */
            BufferedWriter writer = new BufferedWriter(new FileWriter(dbFileName));

            FileWriter myWriter = new FileWriter(dbFileName);
            for (Map.Entry<Integer, Map<String, Object>> model : AllModels.entrySet())
            {
                /* key KVP of model's ID and all of its info */
                int modelID = model.getKey();
                Map<String, Object> currModelInfo = model.getValue();
                modelInfo = "ID: " + modelID;
                /* iterate through all of the model's info and print it to the DB (i.e output file) */
                for (Map.Entry<String, Object> infoOfCurrModel : currModelInfo.entrySet())
                {
                    /* key KVP of model's info and its value */
                    String infoKey = infoOfCurrModel.getKey();
                    Object infoValue = infoOfCurrModel.getValue();
        
                    modelInfo += ", " + infoKey + ": " + infoValue;
                }
                /* each row of the DB (i.e output file) correspond to a model and its info*/
                modelInfo += "\n";
                myWriter.write(modelInfo);
            }
            myWriter.close();

        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private Map<String, Object> getModelInfoAndStore(Field[] publicFields, Class<?> runtimeClassOfGivenClass)
    {
        Map<String, Object> infoOfAModel = new LinkedHashMap<String, Object>();
        String title = null;
        String author = null;
        Object num_copies = 0;
        Object otherField;
        for (Field field : publicFields)
        {
            if (field.isAnnotationPresent(Column.class))
            {
                Class<?> fieldType = field.getType();
                /* ensure that fields that use @Column as annotation can only be String, int, or boolean */
                if (fieldType == String.class || fieldType == int.class || fieldType == boolean.class)
                {
                    try
                    {
                        if (field.get(this) == null)
                        {
                            otherField = "null";
                        }
                        else
                        {
                            otherField = field.get(this);
                        }
                        infoOfAModel.put(field.getName(), otherField);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error in {getModelInfoAndStore} function: " + e);
                    }
                }
                else
                {
                    throw new IllegalArgumentException("Error in {getModelInfoAndStore} function: Field with @Column annotation should be of type Boolean, String, or Integer.");
                }

            }
        }
        infoOfAModel.put("Model_Type", runtimeClassOfGivenClass);
        return infoOfAModel;
    }

    public int id() {
        Model.readAndStoreData();
        return this.currModelID;
    }

    public static <T> T find(Class<T> c, int id) {
        Model.readAndStoreData();
        Object instanceOfAModel = null;
    
        try {
            Map<String, Object> infoOfAModel = AllModels.get(id);
            
            /* given ID does not exist in the DB (i.e output file) */
            if (infoOfAModel == null)
            {
                return null;
            }
            else
            {
                /* get a model with the given ID, from the DB and duplicate (create) a new instance of it */
                Object test = infoOfAModel.get("Model_Type");
                String classType = infoOfAModel.get("Model_Type").toString();
                classType = classType.substring("class ".length());
                Class<?> aModelRuntimeClass = Class.forName(classType);
                Constructor<?> constructorOfGivenClass = aModelRuntimeClass.getDeclaredConstructor();
                instanceOfAModel = constructorOfGivenClass.newInstance();
                String setTo = "";
                /* ensure that the class of the model matches with the given class 'c' */
                if (c.isInstance(instanceOfAModel))
                {
                    /* get all field of the newly duplicated model */
                    Field[] allFields = aModelRuntimeClass.getFields();
                    for (Field field : allFields)
                    {
                        /* ensure that the field is public before accessing it */
                        if (Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                        {
                            /* set the static modelID field of the newly duplicated model to the same value of the exist model that was found from the DB */
                            if (field.getName() == "currModelID")
                            {
                                field.set(instanceOfAModel, id);
                                setTo += ", ID: " + id;
                            }
                            else
                            {
                                if (field.getType().toString().contains("Object"))
                                {
                                    /* set all other public fields of the newly duplicated model to the same value of the exist model that was found from the DB */
                                    Object valueOfAnInfo = infoOfAModel.get(field.getName());
                                    if (valueOfAnInfo == null)
                                    {
                                        field.set(instanceOfAModel, "null");
                                        setTo += ", " + field.getName() + ": " + "null";
                                    }
                                    else
                                    {
                                    field.set(instanceOfAModel, valueOfAnInfo);
                                    setTo += ", " + field.getName() + ": " + valueOfAnInfo;
                                    }
                                }
                                else if (field.getType().toString().contains("String"))
                                {
                                    /* set all other public fields of the newly duplicated model to the same value of the exist model that was found from the DB */
                                    Object valueOfAnInfo = infoOfAModel.get(field.getName());
                                    if (valueOfAnInfo == null)
                                    {
                                        field.set(instanceOfAModel, "null");
                                        setTo += ", " + field.getName() + ": " + "null";
                                    }
                                    else
                                    {
                                        field.set(instanceOfAModel, valueOfAnInfo.toString());
                                        setTo += ", " + field.getName() + ": " + valueOfAnInfo;
                                    }
                                }
                                else if (field.getType().toString().contains("boolean") || (field.getType().toString().contains("Boolean")))
                                {
                                    /* set all other public fields of the newly duplicated model to the same value of the exist model that was found from the DB */
                                    Object valueOfAnInfo = infoOfAModel.get(field.getName());
                                    if (valueOfAnInfo == null)
                                    {
                                        field.set(instanceOfAModel, "null");
                                        setTo += ", " + field.getName() + ": " + "null";
                                    }
                                    else
                                    {
                                        field.set(instanceOfAModel, Boolean.valueOf(valueOfAnInfo.toString()));
                                        setTo += ", " + field.getName() + ": " + valueOfAnInfo;
                                    }
                                }
                                else if ((field.getType().toString().contains("int") || (field.getType().toString().contains("Integer"))))
                                {
                                    /* set all other public fields of the newly duplicated model to the same value of the exist model that was found from the DB */
                                    Object valueOfAnInfo = infoOfAModel.get(field.getName());
                                    if (valueOfAnInfo == null)
                                    {
                                        field.set(instanceOfAModel, "null");
                                        setTo += ", " + field.getName() + ": " + "null";
                                    }
                                    else
                                    {
                                        field.set(instanceOfAModel, Integer.parseInt(valueOfAnInfo.toString()));
                                        setTo += ", " + field.getName() + ": " + valueOfAnInfo;
                                    }
                                }
                                else
                                {
                                    /* set all other public fields of the newly duplicated model to the same value of the exist model that was found from the DB */
                                    Object valueOfAnInfo = infoOfAModel.get(field.getName());
                                    if (valueOfAnInfo == null)
                                    {
                                        field.set(instanceOfAModel, "null");
                                        setTo += ", " + field.getName() + ": " + "null";
                                    }
                                    else
                                    {
                                        field.set(instanceOfAModel, field.getType().cast(valueOfAnInfo));
                                        setTo += ", " + field.getName() + ": " + valueOfAnInfo;
                                    }
                                }
                            }
                        }
                        
                    }
                    /* use c.cast to safely cast the instance to the specified type */
                    T instanceOfAModelClass = c.cast(instanceOfAModel);
                    return instanceOfAModelClass;
                }
                else
                {
                    return null;
                }
                
            }
        } catch (Exception e) {
            System.out.println("Error in {find} function: " + e);
            return null; // Or handle the error as needed
        }
    }

    public static <T> List<T> all(Class<T> c) {
        Model.readAndStoreData();
        List<T> allModelsWithTargetClass = new ArrayList<T>();
        for (Map.Entry<Integer, Map<String, Object>> model : AllModels.entrySet())
        {
            int modelID = model.getKey();
            T instanceOfModel = Model.find(c, modelID);
            if (instanceOfModel != null)
            {
                allModelsWithTargetClass.add(instanceOfModel);
            }
        }
        return allModelsWithTargetClass;
    }

    public void destroy() {
        Model.readAndStoreData();
        Map<String, Object> infoOfAModel = AllModels.get(this.id());
        if (infoOfAModel == null)
        {
            throw new IllegalArgumentException("Error in {destroy} function: this book with ID {" + this.id() + "} does not exist in the DB.");
        }
        else
        {
            AllModels.remove(this.id());
            storeBookInDB(this.currModelID);
        }
    }

    public static void reset() {
        AllModels.clear();
        try
        {
            PrintWriter writer = new PrintWriter(dbFileName);
            writer.print("");
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        AllModels.clear();
    }
}
