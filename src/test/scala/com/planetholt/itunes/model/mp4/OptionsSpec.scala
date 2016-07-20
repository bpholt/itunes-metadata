package com.planetholt.itunes.model.mp4

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import com.planetholt.itunes.model.{Episode ⇒ ModelEpisode, Season ⇒ ModelSeason}
import org.joda.time.DateTime

class OptionsSpec extends Specification with Mockito {
  val unknownSource = "not sure where this will come from yet"

  trait Setup extends Scope {
    val input = ModelEpisode(
      artistId = 1,
      collectionId = 2,
      trackId = 3,
      artistName = "artist",
      collectionName = "show",
      releaseDate = DateTime.now,
      genre = "genre",
      contentAdvisoryRating = "NC17",
      shortDescription = "short",
      longDescription = "long",
      explicit = None,
      discCount = 1,
      discNumber = 1,
      trackCount = 3,
      trackNumber = 2,
      season = ModelSeason(
        artistId = "artistId",
        collectionId = "collectionId",
        artistName = "artistName",
        collectionName = "collectionName",
        copyright = "copyright",
        releaseDate = DateTime.now,
        primaryGenreName = "genre",
        seasonNumber = 4,
        network = Option("network")
      ),
      hdVideo = Option(true),
      picture = Option("file.png")
    )
  }

  "Options" should {
    "format the filename based on padded season and episode numbers" in new Setup {
      val output = Options(input)

      output.input must_== "S04E02.m4v"
    }

    "set the artist id option" in new Setup {
      val output = Options(input)

      output.options must contain(ArtistId(input.artistId))
    }

    "set the playlist id option" in new Setup {
      val output = Options(input)

      output.options must contain(PlaylistId(input.collectionId))
    }

    "set the track id option" in new Setup {
      val output = Options(input)

      output.options must contain(ContentId(input.trackId))
    }

    "set the artist name option" in new Setup {
      val output = Options(input)

      output.options must contain(Artist(input.artistName))
    }

    "set the album name option" in new Setup {
      val output = Options(input)

      output.options must contain(Album(input.collectionName))
    }

    "set the release date option" in new Setup {
      val output = Options(input)

      output.options must contain(Year(input.releaseDate))
    }

    "set the genre option" in new Setup {
      val output = Options(input)

      output.options must contain(Genre(input.genre))
    }

    "set the short description option" in new Setup {
      val output = Options(input)

      output.options must contain(Description(input.shortDescription))
    }

    "set the long description option" in new Setup {
      val output = Options(input)

      output.options must contain(LongDesc(input.longDescription))
    }

    "set the disc count option" in new Setup {
      val output = Options(input)

      output.options must contain(Disks(input.discCount))
    }

    "set the disc number option" in new Setup {
      val output = Options(input)

      output.options must contain(Disk(input.discNumber))
    }

    "set the track count option" in new Setup {
      val output = Options(input)

      output.options must contain(Tracks(input.trackCount))
    }

    "set the disc number option" in new Setup {
      val output = Options(input)

      output.options must contain(Track(input.trackNumber))
    }

    "set the season option" in new Setup {
      val output = Options(input)

      output.options must contain(Season(input.season.seasonNumber))
    }

    "set the episode option" in new Setup {
      val output = Options(input)

      output.options must contain(Episode(input.trackNumber))
    }

    "not set the explicit option" in new Setup {
      val output = Options(input)

      output.options must not(contain(Explicit))
    }

    "not set the clean option" in new Setup {
      val output = Options(input)

      output.options must not(contain(Clean))
    }

    "when the content is cleaned" >> {
      "set the clean option" in new Setup {
        val output = Options(input.copy(explicit = Some(false)))

        output.options must contain(Clean)
      }

      "not set the explicit option" in new Setup {
        val output = Options(input.copy(explicit = Some(false)))

        output.options must not(contain(Explicit))
      }
    }

    "when the content is explicit" >> {
      "set the explicit option" in new Setup {
        val output = Options(input.copy(explicit = Some(true)))

        output.options must contain(Explicit)
      }

      "not set the clean option" in new Setup {
        val output = Options(input.copy(explicit = Some(true)))

        output.options must not(contain(Clean))
      }
    }

    "season-level options" >> {
      "set the copyright details" in new Setup {
        val output = Options(input)

        output.options must contain(Copyright(input.season.copyright))
      }

      "HD video options" >> {
        "set the HD video flag" in new Setup {
          val output = Options(input)

          output.options must contain(HdVideo(true))
        }

        "map the HD video flag to 0 if it's set to false" in new Setup {
          HdVideo(false).toString must_== "-hdvideo 0"
        }

        "map the HD video flag to 1 if it's set to true" in new Setup {
          HdVideo(true).toString must_== "-hdvideo 1"
        }
      }

      "artwork options" >> {
        "set the path to the artwork" in new Setup {
          val output = Options(input)

          output.options must contain(Picture("file.png"))
        }

        "not include picture option if it is not set" in new Setup {
          val output = Options(input.copy(picture = None))

          output.options must not(contain(anInstanceOf[Picture]))
        }
      }

      "network options" >> {
        "set the network" in new Setup {
          val output = Options(input)

          output.options must contain(Network(input.season.network.get))
        }

        "not include network option if it is not set" in new Setup {
          val output = Options(input.copy(season = input.season.copy(network = None)))

          output.options must not(contain(anInstanceOf[Network]))
        }
      }

    }

    "remove encodedby and tool" in new Setup {
      val output = Options(input)

      output.options must contain(Remove("eE"))
    }

  }

}
