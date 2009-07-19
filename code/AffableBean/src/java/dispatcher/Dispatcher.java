package dispatcher;

import cart.ShoppingCart;
import database.AffableBeanDBAO;
import entity.Category;
import entity.Product;
import exceptions.BadInputException;
import exceptions.CategoryNotFoundException;
import exceptions.ProductNotFoundException;
import exceptions.ProductsNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author nbuser
 */
public class Dispatcher extends HttpServlet {

//    @Resource
//    private UserTransaction utx;
    private AffableBeanDBAO affableBeanDBAO;
    private Category selectedCategory;
    private List categoryProducts;
    private ShoppingCart cart;
    private String requestedPath;

    @Override
    public void init() {

        // gets DBAO instance from servlet context
        affableBeanDBAO = (AffableBeanDBAO) getServletContext().getAttribute("affableBeanDBAO");
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        requestedPath = request.getServletPath();
        cart = (ShoppingCart) session.getAttribute("cart");

        String clear;

        if (cart == null) {
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }

        // if category page is requested
        if (requestedPath.equals("/category")) {

            // get categoryId from request
            String categoryId = request.getQueryString();

            if (categoryId != null) {

                // synchronize HttpSession object to protect session attributes
                synchronized (session) {
                    // get selected category
                    try {
                        selectedCategory = affableBeanDBAO.getCategory(categoryId);
                    } catch (CategoryNotFoundException cnfe) {
                        System.err.println("Unable to get category. " + cnfe.getMessage());
                    }

                    // place selected category in session scope
                    session.setAttribute("selectedCategory", selectedCategory);

                    // get all products for selected category
                    try {
                        categoryProducts = affableBeanDBAO.getCategoryProducts(categoryId);
                    } catch (ProductsNotFoundException pnfe) {
                        System.err.println("Unable to get products. " + pnfe.getMessage());
                    }

                    // place category products in session scope
                    session.setAttribute("categoryProducts", categoryProducts);
                }
            }

            // if shopping cart page is requested
        } else if (requestedPath.equals("/viewCart")) {

            requestedPath = "/cart";

            clear = request.getParameter("clear");

            if ((clear != null) && clear.equals("true")) {
                cart.clear();
            }

            // if checkout page is requested
        } else if (requestedPath.equals("/checkout")) {

            // forward to /WEB-INF/jsp/checkout.jsp
            // switch to https protocol
            
        } else if (requestedPath.equals("/languageChoice")) {

            // change this!
            requestedPath = "/category";

            // get language choice
            String language = request.getParameter("language");

            // place in session scope
            session.setAttribute("language", language);

//        // return user from whence s/he came
//        // note: this doesn't work - getting the referer isn't reliable
//        String url = request.getHeader("Referer");

//        String url = request.getContextPath();  !try this!

//        System.out.println(url);
//
//        url = url.substring(url.lastIndexOf('/'));
//        System.out.println(url);

        }

        // use RequestDispatcher to forward request internally
        String url = "/WEB-INF/jsp" + requestedPath + ".jsp";

        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        requestedPath = request.getServletPath();
        cart = (ShoppingCart) session.getAttribute("cart");

        // get user input from request
        String productId = request.getParameter("productId");
        String quantity = request.getParameter("quantity");

        Product product;

        // if addToCart action is called
        if (requestedPath.equals("/addToCart")) {

            requestedPath = "/category";

            if (!productId.equals("")) {

                try {
                    product = affableBeanDBAO.getProduct(productId);

                    cart.add(productId, product);

                } catch (ProductNotFoundException pnfe) {

                    System.err.println("Unable to add product to cart. " + pnfe.getMessage());
                }
            }

            // if updateCart action is called
        } else if (requestedPath.equals("/updateCart")) {

            requestedPath = "/cart";

            if (!productId.equals("") && !quantity.equals("")) {

                try {
                    product = affableBeanDBAO.getProduct(productId);

                    cart.update(productId, product, quantity);

                } catch (BadInputException bne) {

                    System.err.println("Unable to complete updateCart action. " + bne.getMessage());

                } catch (ProductNotFoundException pnfe) {

                    System.err.println("Unable to update cart. " + pnfe.getMessage());
                }
            }

            // if purchase action is called
        } else if (requestedPath.equals("/purchase")) {

            // extract user data from request
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String cityRegion = request.getParameter("cityRegion");
            String ccNumber = request.getParameter("ccNumber");

            
        }

        // use RequestDispatcher to forward request internally
        String url = "/WEB-INF/jsp" + requestedPath + ".jsp";

        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}