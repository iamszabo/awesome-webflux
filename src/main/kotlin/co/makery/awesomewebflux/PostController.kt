package co.makery.awesomewebflux

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

@Document
data class Post(val userId: Int, @Id val id: Int, val title: String, val body: String)

interface PostRepository : ReactiveMongoRepository<Post, Int> {}

@RestController
class PostController(private val postRepository: PostRepository) {
  @GetMapping("/posts/{id}")
  fun get(@PathVariable id: String) = getPost(id).flatMap { insertPost(it) }

  private fun getPost(id: String) = WebClient.create("https://jsonplaceholder.typicode.com/posts/$id").get().retrieve().bodyToMono(Post::class.java)

  private fun insertPost(post: Post) = postRepository.findById(post.id).switchIfEmpty(postRepository.insert(post))
}
