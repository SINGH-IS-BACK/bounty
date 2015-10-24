package controllers;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseController extends Controller {
	
    private static final String ERROR_TAG = "error";

	public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Status generateInternalServer(String msg){
		ObjectNode result = Json.newObject();
		result.put(ERROR_TAG, msg);
		return internalServerError(result);
	}
	
    public static Status generateBadRequest(String msg){
		ObjectNode result = Json.newObject();
		result.put(ERROR_TAG, msg);
		return badRequest(result);
	}
	
    public static Status generateUnauthorizedRequest(String msg){
		ObjectNode result = Json.newObject();
		result.put(ERROR_TAG, msg);
		return unauthorized(result);
	}
	
    public static Status generateOkTrue(){
		ObjectNode result = Json.newObject();
		result.put("ok", true);
		return ok(result);
	}
}
