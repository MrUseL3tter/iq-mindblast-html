<?php
/**
* Copyright 2012 Facebook, Inc.
*
* You are hereby granted a non-exclusive, worldwide, royalty-free license to
* use, copy, modify, and distribute this software in source code or binary
* form for use in connection with the web services and APIs provided by
* Facebook.
*
* As with any software that integrates with the Facebook platform, your use
* of this software is subject to the Facebook Developer Principles and
* Policies [http://developers.facebook.com/policy/]. This copyright notice
* shall be included in all copies or substantial portions of the software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
* THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
* DEALINGS IN THE SOFTWARE.
*/

  require 'server/fb-php-sdk/facebook.php';

  $app_id = '360919987315515';
  $app_secret = '49ca8147931e5401206a08a92a2bf25c';
  $app_namespace = 'iqmindblast';
  $app_url = 'http://apps.facebook.com/' . $app_namespace . '/';
  $scope = 'email,publish_actions';

  // Init the Facebook SDK
  $facebook = new Facebook(array(
    'appId'  => $app_id,
    'secret' => $app_secret,
  ));

  // Get the current user
  $user = $facebook->getUser();

  // If the user has not installed the app, redirect them to the Auth Dialog
  if (!$user) {
    $loginUrl = $facebook->getLoginUrl(array(
      'scope' => $scope,
      'redirect_uri' => $app_url,
    ));

    print('<script> top.location.href=\'' . $loginUrl . '\'</script>');
  }

  if(isset($_REQUEST['request_ids'])) {
    $requestIDs = explode(',' , $_REQUEST['request_ids']);
    foreach($requestIDs as $requestID) {
      try {
        $delete_success = $facebook->api('/' . $requestID, 'DELETE');
      } catch(FacebookAPIException $e) {
        error_log($e);
      }
    }
  }
?>

<!doctype html>
<html>
	<head>
		<title>IQ Mindblast</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<style>
			canvas {
				outline: none;
			}
		</style>
        
        <script>
			function publishFeed(score) {
				// post a feed
   				var messageStr = 'I scored ' + score + ' in IQ Mindblast!';
				FB.ui({ method: 'feed',
     				caption: messageStr,
     				name: 'Play IQ Mindblast now!',
     				picture: 'http://i.imgur.com/di3YI.jpg',
     				link: 'http://apps.facebook.com/iqmindblast'
   				}, fbCallback);
			}
			
			function publishScore(scoreValue) {
				//save the score
				FB.api('/me/scores', 'post', params = { score: scoreValue }, 
					function(response) {
   					}
				);
			}
			
			function fbCallback(response) {
 			}
		
		</script>
	</head>
	
	<body>  
  		<script src="//connect.facebook.net/en_US/all.js"></script>
		<script>
    		var appId = '<?php echo $facebook->getAppID() ?>';

   		 	// Initialize the JS SDK
    		FB.init({
      			appId: appId,
      			cookie: true,
    		});

    		FB.getLoginStatus(function(response) {
      			uid = response.authResponse.userID ? response.authResponse.userID : null;
    		});
  		</script>
		<div align="center" id="embed-org.neu.cs.cs434.GwtDefinition"></div>
		<script type="text/javascript" src="org.neu.cs.cs434.GwtDefinition/org.neu.cs.cs434.GwtDefinition.nocache.js"></script>
		<script type="text/javascript" src="scripts/fb.js"></script>
	</body>
	
	<script>
		function handleMouseDown(evt) {
		  evt.preventDefault();
		  evt.stopPropagation();
		}
		
		function handleMouseUp(evt) {
		  evt.preventDefault();
		  evt.stopPropagation();
		  evt.target.style.cursor = '';
		}
		
		document.getElementById('embed-org.neu.cs.cs434.GwtDefinition').addEventListener('mousedown', handleMouseDown, false);
		document.getElementById('embed-org.neu.cs.cs434.GwtDefinition').addEventListener('mouseup', handleMouseUp, false);
	</script>
</html>
