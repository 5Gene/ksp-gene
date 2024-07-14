package gene.net.repository.ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate

const val NET_REPO_ANNO = "gene.net.repository.NetRepository"

@AutoService(SymbolProcessorProvider::class)
class NetRepositorySymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return NetRepositorySymbolProcessor(environment)
    }
}

class NetRepositorySymbolProcessor(val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbolsWithAnnotation = resolver.getSymbolsWithAnnotation(NET_REPO_ANNO)
        if (symbolsWithAnnotation.toList().isEmpty()) {
            return emptyList()
        }
        symbolsWithAnnotation.filter { it.validate() }.forEach {

//            public interface GitHubService {
//  @GET("users/{user}/repos")
//  Call<List<Repo>> listRepos(@Path("user") String user);
//}
            //@POST("users/new")
            //fun createUser(@Body user: HashMap<String, Any>): Call<User>
//            GitHubService service = retrofit.create(GitHubService.class);

        }
        return emptyList()
    }
}