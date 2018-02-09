package com.sugarnaoming.chachamaru.errors

import java.io.IOException

object ErrorMessages {
  private const val UNEXPECTED_ERROR_MESSAGE = "予期せぬエラーが発生しました"

  fun httpError(code: Int): String {
    val TYPE_CLIENT_ERROR = 4
    val TYPE_SERVER_ERROR = 5
    val errorType = Integer.parseInt(code.toString()[0].toString())
    return when(errorType) {
      TYPE_CLIENT_ERROR -> clientErrorMessage(code)
      TYPE_SERVER_ERROR -> serverErrorMessage(code)
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }

  fun networkError(t: Throwable): String {
    return if(t is IOException) "ネットワークに接続できませんでした"
    else UNEXPECTED_ERROR_MESSAGE
  }

  fun urlParseError(): String = "正しく無いURLです"

  fun unexpectedError(): String = UNEXPECTED_ERROR_MESSAGE

  fun groupError(errorType: Int): String {
    return when(errorType) {
      GroupError.NAME_BLANK -> "グループの名前を空にはできません\n空白文字だけもダメですよ"
      GroupError.NAME_DUPLICATE -> "既に同じ名前のグループが存在します\n新しいグループ名を考えてみてください"
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }

  fun feedError(errorType: Int): String {
    return when(errorType) {
      FeedError.PARSE_ERROR -> "RSSまたはatomの解析に失敗しました\nRSSまたはatomが提供されていないかもしれません"
      FeedError.FEED_TYPE_NOT_UNDERSTAND -> "フィードの解析に使用する型が間違っています" // これが起きたらコードが間違っている
      FeedError.FILE_NOT_FOUND -> "ファイルを見つけることができませんでした" // ローカルファイルは行っていないので起きることは無い
      FeedError.PROBLEM_READ_FILE -> "ファイルの読み込みに失敗しました" // ローカルファイルは行っていないので起きることは無い
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }

  fun tabError(errorType: Int): String {
    return when(errorType) {
      TabError.NAME_DUPLICATE -> "グループ内でタブの名前が重複しています\n新しいタブ名を考えて見てください"
      TabError.NAME_OR_URL_BLANK -> "タブの名前とURLは空にできません\n空白文字だけも許しませんよ"
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }

  fun StockError(): String = "既に「後で読む」に登録されています"

  private fun clientErrorMessage(code: Int): String {
    return when(code) {
      403 -> "アクセスが禁止されています"
      404 or 410 or 423 -> "サイトが見つかりませんでした\nURLが間違っているか、サイトがなくなっています"
      408 -> "リクエストがタイムアウトしました"
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }

  private fun serverErrorMessage(code: Int): String {
    return when(code) {
      500 -> "アクセス先のサーバーでエラーが起きました"
      503 -> "アクセス先のサーバーでエラーが起きました"
      else -> UNEXPECTED_ERROR_MESSAGE
    }
  }
}