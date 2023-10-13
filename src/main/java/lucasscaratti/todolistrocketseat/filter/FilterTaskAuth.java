package lucasscaratti.todolistrocketseat.filter;


import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lucasscaratti.todolistrocketseat.users.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //Pegar autenticação (Usuario / senha)
        var authorization = request.getHeader("Authorization");
        System.out.println("Authorization");
        System.out.println(authorization);


        var authEncoded = authorization.substring("Basic".length()).trim();

        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

        var authString = new String(authDecoded);

        //["lucasscaratti", "1234"]
        String[] credentials = authString.split(":");
        String username = credentials[0];
        String password = credentials[1];

        //Validar Usuario
        var user = this.userRepository.findByUsername(username);
        if(user == null) {
            response.sendError(401);
        } else {
            //Validar Senha
            var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if(passwordVerify.verified) {
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401);
            }
            //Segue viagem
        }
    }
}
