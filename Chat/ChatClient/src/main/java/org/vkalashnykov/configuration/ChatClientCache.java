package org.vkalashnykov.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vkalashnykov on 31.12.16.
 */
public class ChatClientCache {

    private static String currentUserUsername;
    private static Map<String,String> currentUserProfile=new HashMap<>();
    private static String currentUserStatus;

    private static List<String> channels;
    private static String currentChannel;
    private static List<String> usersOnChannel;

    private  static String profileUsername;
    private static Map<String,String> userProfile;
    private static String profileUserStatus;

    private static Map<String,String>  channelDetails;

    public static String getCurrentChannel() {
        return currentChannel;
    }

    public static void setCurrentChannel(String currentChannel) {
        ChatClientCache.currentChannel = currentChannel;
    }

    public static List<String> getUsersOnChannel() {
        return usersOnChannel;
    }

    public static void setUsersOnChannel(List<String> usersOnChannel) {
        ChatClientCache.usersOnChannel = usersOnChannel;
    }

    public static Map<String, String> getChannelDetails() {
        return channelDetails;
    }

    public static void setChannelDetails(Map<String, String> channelDetails) {
        ChatClientCache.channelDetails = channelDetails;
    }

    public static String getCurrentUserUsername() {
        return currentUserUsername;
    }

    public static void setCurrentUserUsername(String currentUserUsername) {
        ChatClientCache.currentUserUsername = currentUserUsername;
    }

    public static Map<String, String> getCurrentUserProfile() {
        return currentUserProfile;
    }

    public static void setCurrentUserProfile(Map<String, String> currentUserProfile) {
        ChatClientCache.currentUserProfile = currentUserProfile;
    }

    public static String getCurrentUserStatus() {
        return currentUserStatus;
    }

    public static void setCurrentUserStatus(String currentUserStatus) {
        ChatClientCache.currentUserStatus = currentUserStatus;
    }

    public static List<String> getChannels() {
        return channels;
    }

    public static void setChannels(List<String> channels) {
        ChatClientCache.channels = channels;
    }

    public static void cleanCache(){
        setCurrentUserProfile(null);
        setCurrentUserUsername(null);
        setCurrentUserStatus(null);
        setChannels(null);
        setChannelDetails(null);
        setCurrentChannel(null);
        setUsersOnChannel(null);
    }

    public static String getUsername() {
        return profileUsername;
    }

    public static void setUsername(String username) {
        ChatClientCache.profileUsername = username;
    }

    public static Map<String, String> getUserProfile() {
        return userProfile;
    }

    public static void setUserProfile(Map<String, String> userProfile) {
        ChatClientCache.userProfile = userProfile;
    }

    public static String getProfileUserStatus() {
        return profileUserStatus;
    }

    public static void setProfileUserStatus(String profileUserStatus) {
        ChatClientCache.profileUserStatus = profileUserStatus;
    }

    public static boolean isCurrentUser(){
        return profileUsername.equals(currentUserUsername) ? true : false;
    }

    public static boolean isAdmin(){
        return currentUserStatus.equals(UserStatuses.ADMIN.getRolename());
    }

    public static boolean isModerator(){
        return currentUserStatus.equals(UserStatuses.MODERATOR.getRolename());
    }

    public static boolean isVIP(){
        return currentUserStatus.equals(UserStatuses.VIP.getRolename());
    }

    public static boolean isProfileAdmin(String username){
        return profileUserStatus.equals(UserStatuses.ADMIN.getRolename());
    }

    public static boolean isProfileModerator(String username){
        return profileUserStatus.equals(UserStatuses.MODERATOR.getRolename());
    }

    public static boolean isProfileVIP(String username){
        return profileUserStatus.equals(UserStatuses.VIP.getRolename());
    }

}
