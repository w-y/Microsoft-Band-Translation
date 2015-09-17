<?php

    include 'HTTPRequest.php';
    include 'AccessTokenAuthentication.php';
    include 'config.php';

    try {
        //Create the AccessTokenAuthentication object.
        $authObj      = new AccessTokenAuthentication();
        //Get the Access token.
        $accessToken  = $authObj->getTokens($grantType, $scopeUrl, $clientID, $clientSecret, $authUrl);
        //Create the authorization Header string.
        $authHeader = "Authorization: Bearer ". $accessToken;

        //Set the params.//
        $fromLanguage = "zh-CHS";
        $toLanguage   = "en";
        $contentType  = 'text/html';
        $category     = 'general';

        $translateUrl = "http://api.microsofttranslator.com/v2/Http.svc/TranslateArray";
        $rssUrl       = "https://www.v2ex.com/api/topics/latest.json";

        //Create the v2ex Object.
        $rssObj = new HTTPRequest();

        //Get v2ex rss
        $rssResponse = $rssObj->curlRequest($rssUrl, NULL, NULL);
        $jsonObj = json_decode($rssResponse);

        $resObj = new stdClass();
        $resObj->posts = new stdClass();

        $resObj->status = 'ok';

        for ($i= 0; $i< min(5, count($jsonObj)); $i++) {

            $inputStrArr = array();

            $itemObj = new stdClass();

            $title = (String)$jsonObj[$i]->title;
            $content = (String)$jsonObj[$i]->content;

            $inputStrArr []= $title;
            $inputStrArr []= preg_replace('/(?:\\\u000D) | (?:\\\u000A)/', '', $content);

            //Create the Translator Object.
            $translatorObj = new HTTPRequest();

            $requestXml = $translatorObj->createReqXML($fromLanguage, $toLanguage, $contentType, $inputStrArr);

            //Get the curlResponse.
            $curlResponse = $translatorObj->curlRequest($translateUrl, $authHeader, $requestXml);

            //Interprets a string of XML into an object.
            $xmlObj = simplexml_load_string($curlResponse);
            $ii = 0;

            $itemObj->id = $i;

            $itemObj->trans_title = 'empty';
            $itemObj->trans_content = 'empty';

            foreach($xmlObj->TranslateArrayResponse as $translatedArrObj){

                if ($ii === 0) {
                    $itemObj->trans_title = (String)$translatedArrObj->TranslatedText;
                } else {
                    $itemObj->trans_content = (String)$translatedArrObj->TranslatedText;
                }
                $ii++;
            }
            $resObj->posts->{$i} = $itemObj;
        }

        echo json_encode($resObj, JSON_PRETTY_PRINT);

    } catch (Exception $e) {
        echo "Exception: " . $e->getMessage() . PHP_EOL;
        die();
    }
