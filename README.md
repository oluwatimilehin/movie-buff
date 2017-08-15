# Movie Buff
Movie Buff displays the top rated and most popular movies today and lets users store their favorite movies in a SQLite Database using ContentProviders. 

If you would like to try it out, you can download the apk from [this link.](https://drive.google.com/open?id=0B2XA6b4G9Lf-TzAwempHM0Z3VDQ) 

It fetches data from TheMovieDatabase API. You would have to generate your own API key and use it in the MovieActivity.java file. 
Users have the option to sort movies by 'Popular','Top rated' and 'Favorites' . Selecting a movie loads up a new activity that displays details of that movie. 

The DetailActivity makes two separate API calls to get the youtube key and the reviews of the selected movie and returns the data in a bundle. Adding a movie to favorites stores all the information related to the movie in the database making it all available offline. 

There is also a 'Read More' button added below Reviews that are a few lines too long with an animation that expands to show the full review. 

It makes use of the Picasso library to load images and the Calligraphy library for custom fonts. 

### Screenshots 

Popular Movies:

![Popular](/screenshots/popular.png "Popular Movies")

Menu items:

![Sort by](/screenshots/menu.png)

Top Rated Movies:

![Top rated](/screenshots/top-rated.png)

Favorites:

![Favorite Movies](/screenshots/favorites.png)

Details Screen:

![Detail](/screenshots/detail.png)

Error Screen:

![Error](/screenshots/error.png)




