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
        $fromLanguage = "";
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

        $size = count($jsonObj);

        for ($i= 0; $i< $size; $i++) {

            $inputStrTitleArr = array();
            $inputStrContentArr = array();
            $count = 0;

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

            foreach($xmlObj->TranslateArrayResponse as $translatedArrObj){

                if ($ii === 0) {
                    $jsonObj[$i]->transtitle = (String)$translatedArrObj->TranslatedText;
                } else {
                    $jsonObj[$i]->transcontent = (String)$translatedArrObj->TranslatedText;
                }
                $ii++;
            }

        }

        echo json_encode($jsonObj);

    } catch (Exception $e) {
        echo "Exception: " . $e->getMessage() . PHP_EOL;
    }
