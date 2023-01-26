package umm3601.todo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of user info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of user data from a specified JSON file, and
 * then provide various database-like methods that allow the `UserController` to
 * "query" the "database".
 */
public class TodoDatabase{

  private Todo[] allTodos;

  public TodoDatabase(String TodoDataFile) throws IOException {
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(TodoDataFile));
    ObjectMapper objectMapper = new ObjectMapper();
    allTodos = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }


  /**
   * Get the single user specified by the given ID. Return `null` if there is no
   * todo with that ID.
   *
   * @param id the ID of the desired user
   * @return the todo with the given ID, or null if there is no user with that ID
   */
  public Todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }
  /**
   * Get an array of all the users satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the users matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    //   // Filter status if defined
    //  if (queryParams.containsKey("status")) {
    //    Boolean statusParam = queryParams.get("status").get(0);
    //    try {
    //      int targetStatus = Boolean.parseBoolean(statusParam);
    //      filteredTodos = filterTodosByStatus(filteredTodos, targetStatus);
    //    } catch (BooleanFormatException e) {
    //      throw new BadRequestResponse("Specified age '" + statusParam + "' can't be parsed to an integer");
    //    }
    //  }

      // Filter owner if defined
      if (queryParams.containsKey("owner")) {
        String targetOwner = queryParams.get("owner").get(0);
        filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
      }
      // Process other query parameters here...

        // Filter category if defined
        if (queryParams.containsKey("category")) {
          String targetCategory = queryParams.get("category").get(0);
          filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
        }
        // Process other query parameters here...

        // // limit number of todos displayed

        if (queryParams.containsKey("limit")) {
          String targetLimit = queryParams.get("limit").get(0);
          filteredTodos = filterTodosByLimit(filteredTodos, targetLimit);
        }

          // Support searching for bodies containing given string
          if (queryParams.containsKey("contains")) {
            String targetContains = queryParams.get("contains").get(0);
            filteredTodos = filterTodosByContains(filteredTodos, targetContains);
          }

          // support ordering/sorting of todos by attribute

          if (queryParams.containsKey("orderBy")){
            String targetOrder = queryParams.get("orderBy").get(0);
            filteredTodos = filterTodosByOrder(filteredTodos, targetOrder);
          }


      return filteredTodos;
    }


    public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
      return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory) {
      return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByContains(Todo[] todos, String targetContains) {
      return Arrays.stream(todos).filter(x -> x.body.contains(targetContains)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByLimit(Todo[] todos, String targetLimit) {
      return Arrays.stream(todos).limit(Integer.valueOf(targetLimit)).toArray(Todo[]::new);
    }

    public Todo[] filterTodosByOrder(Todo[] todos, String targetOrder) {
      if (targetOrder.equals("owner")){

        return Arrays.stream(todos).sorted(Comparator.comparing((Todo t) -> t.owner)).toArray(Todo[]::new);
      }
      else if (targetOrder.equals("body")){
        return Arrays.stream(todos).sorted(Comparator.comparing((Todo t) -> t.body)).toArray(Todo[]::new);

      }
      else if (targetOrder.equals("status")){
        return Arrays.stream(todos).sorted(Comparator.comparing((Todo t) -> t.status)).toArray(Todo[]::new);
      }
      else {
        return Arrays.stream(todos).sorted(Comparator.comparing((Todo t) -> t.category)).toArray(Todo[]::new);
      }
    }





}
