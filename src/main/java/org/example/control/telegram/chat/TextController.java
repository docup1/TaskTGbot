package org.example.control.telegram.chat;

public class TextController {
//
//    private Response start(String user){
//        var list = new ArrayList<String>();
//        list.add(user);
//        Logger.put("Start arguments: " + list.toString(), LogType.TRACE);
//        var res = mannager.execute(CommandsNames.ADD_USER, list);
//        Logger.put("Executed command: " + CommandsNames.ADD_USER + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//
//    private Response newTask(String userName){
//        var args = new ArrayList<String>();
//        var userID = mannager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
//        args.add(userID);
//        args.add(converter.getArgs().get(0));
//        var res = mannager.execute(CommandsNames.CREATE_TASK, args);
//        Logger.put("Executed command: " + CommandsNames.CREATE_TASK + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//
//    private Response setBio(String userName){
//        var args = new ArrayList<String>();
//        args.add(userName);
//        args.addAll(converter.getArgs());
//        var res = mannager.execute(CommandsNames.UPDATE_BIO, args);
//        Logger.put("Executed command: " + CommandsNames.UPDATE_BIO + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//
//    private Response userInf(String userName){
//        var args = new ArrayList<String>();
//        args.add(userName);
//        var res = mannager.execute(CommandsNames.USER_INF, args);
//        Logger.put("Executed command: " + CommandsNames.USER_INF + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//
//    private Response myTasks(String userName){
//        var args = new ArrayList<String>();
//        var userID = mannager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
//        args.add(userID);
//        args.addAll(converter.getArgs());
//        var res = mannager.execute(CommandsNames.MY_TASKS, args);
//        Logger.put("Executed command: " + CommandsNames.MY_TASKS + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//
//    private Response setDeadline(String userName){
//        var args = new ArrayList<String>();
//        var userID = mannager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
//        args.add(userID);
//        args.addAll(converter.getArgs());
//        var res = mannager.execute(CommandsNames.SET_DEADLINE, args);
//        Logger.put("Executed command: " + CommandsNames.SET_DEADLINE + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//    private Response setDescr(String userName){
//        var args = new ArrayList<String>();
//        var userID = mannager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
//        args.add(userID);
//        args.addAll(converter.getArgs());
//        var res = mannager.execute(CommandsNames.SET_DESCRIPTION, args);
//        Logger.put("Executed command: " + CommandsNames.SET_DESCRIPTION + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }
//    private Response setRew(String userName){
//        var args = new ArrayList<String>();
//        var userID = mannager.execute(CommandsNames.GET_USER, new ArrayList<>(Arrays.asList(userName))).getData().toString();
//        args.add(userID);
//        args.addAll(converter.getArgs());
//        var res = mannager.execute(CommandsNames.SET_REWARD, args);
//        Logger.put("Executed command: " + CommandsNames.SET_REWARD + " " + res.getStringStatus(), LogType.INFO);
//        return res;
//    }

//            converter.run(messageText);
//            var command = converter.getCommandName();
//            switch (command){
//                case "/start":
//                    output = start(userName);
//                    break;
//                case "/newtask":
//                    output = newTask(userName);
//                    break;
//                case "/mytasks":
//                    output = myTasks(userName);
//                    break;
//                case "/userinf":
//                    output = userInf(userName);
//                    break;
//                case "/setbio":
//                    output = setBio(userName);
//                    break;
//
//                case "/setdealine":
//                    output = setDeadline(userName);
//                    break;
//                case "/setdescr":
//                    converter.run(messageText, true);
//                    output = setDescr(userName);
//                    break;
//                case "/setrew":
//                    output = setRew(userName);
//                    break;
//
//                case "/help":
//                    output = newTask(userName);
//                    break;
//                default:
//                    output = new Response(Status.NO_CONTENT);
//                    Logger.put(output.getStringStatus(), LogType.ERROR);
//                    needToSend = false;
//                    break;
//            }
}
