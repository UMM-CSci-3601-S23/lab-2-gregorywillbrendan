package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.http.Context;


import umm3601.Server;

/**
 * Tests the logic of the UserController
 *
 * @throws IOException
 */
// The tests here include a ton of "magic numbers" (numeric constants).
// It wasn't clear to me that giving all of them names would actually
// help things. The fact that it wasn't obvious what to call some
// of them says a lot. Maybe what this ultimately means is that
// these tests can/should be restructured so the constants (there are
// also a lot of "magic strings" that Checkstyle doesn't actually
// flag as a problem) make more sense.
@SuppressWarnings({ "MagicNumber" })
public class TodoControllerSpec {

  private Context ctx = mock(Context.class);

  private TodoController todoController;
  private static TodoDatabase db;

  @BeforeEach
  public void setUp() throws IOException {
    db = new TodoDatabase(Server.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }

  /**
   * Confirms that we can get all the todos.
   *
   * @throws IOException
   */
  @Test
  public void canGetAllTodos() throws IOException {
    // Call the method on the mock context, which doesn't
    // include any filters, so we should get all the todos
    // back.
    todoController.getTodos(ctx);

    // Confirm that `json` was called with all the users.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
  }

  @Test
  public void canGetTodosWithOwnerFry() throws IOException {
    // Add a query param map to the context that maps "owner"
    // to "Fry".
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Fry"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    // Call the method on the mock controller with the added
    // query param map to limit the result to just todos with
    // owner "Fry".
    todoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have owner "fry".
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Fry", todo.owner);
    }
  }
  @Test
  public void canGetTodosWithCategoryHomework() throws IOException {
    // this tests that filtering by category
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"homework"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    // Call the method on the mock controller with the added
    // query param map to limit the result to just todos with
    // owner "Fry".
    todoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have owner "fry".
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("homework", todo.category);
    }
  }


  @Test
  public void canGetTodosWithGivenCategoryAndOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"groceries"}));
    queryParams.put("owner", Arrays.asList(new String[] {"Fry"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the todos passed to 'json' have category groceries
    // and have the owner Fry.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("groceries", todo.category);
      assertEquals("Fry", todo.owner);
    }
    assertEquals(17, argument.getValue().length);
  }
}
